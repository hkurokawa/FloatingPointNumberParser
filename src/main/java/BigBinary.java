import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

public class BigBinary implements BigNumber {
  private final LinkedList<Integer> digits = new LinkedList<>(); // Digits from right to left
  private boolean negative;
  private int dp; // Location of decimal point from right, e.g. dp = 4 for 1.0001

  public BigBinary(int n) {
    if (n < 0) {
      negative = true;
      n *= -1;
    }
    while (n > 0) {
      digits.add(n % 2);
      n /= 2;
    }
    normalize();
  }

  public BigBinary(String s) {
    if (s == null || s.isEmpty()) {
      throw new IllegalArgumentException("Cannot parse an empty string");
    }
    if (s.startsWith("-")) {
      negative = true;
      s = s.substring(1);
    }
    if (s.startsWith("+")) {
      s = s.substring(1);
    }
    if (!s.startsWith("0x")) {
      throw new IllegalArgumentException("The string must start with 0x: " + s);
    }
    s = s.substring(2);
    s = s.replace("_", "");
    dp = -1;
    int exp = 0;
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      int b = convertHexToInt(ch);
      if (ch == '.') {
        if (dp >= 0) {
          throw new IllegalArgumentException("Unexpected decimal point at "
              + i + ". There are more than one decimal points: " + s);
        }
        dp = i * 4;
      } else if (b >= 0) {
        digits.add(b >> 3 & 1);
        digits.add(b >> 2 & 1);
        digits.add(b >> 1 & 1);
        digits.add(b & 1);
      } else if (ch == 'p' || ch == 'P') {
        exp = Integer.parseInt(s, i + 1, s.length(), 10);
        i = s.length();
      } else {
        throw new IllegalArgumentException("Unexpected char at " + i + ": " + s);
      }
    }
    if (dp < 0) {
      dp = digits.size();
    }
    Collections.reverse(digits);
    dp = digits.size() - dp;
    dp -= exp;
    normalize();
  }

  @Override public boolean isNegative() {
    return negative;
  }

  @Override public void multiplyByTwo() {
    dp--;
    normalize();
  }

  @Override public void divideByTwo() {
    dp++;
    normalize();
  }

  @Override public boolean isLessThanOne() {
    return dp == digits.size() - 1 && digits.getLast() == 0;
  }

  @Override public boolean isEqualToOrGreaterThanTwo() {
    if (dp != digits.size() - 1) return true; // >= 10
    return digits.getLast() > 1;
  }

  @Override public void discardNumberPart() {
    while (dp < digits.size() - 1) digits.removeLast();
    digits.set(digits.size() - 1, 0);
  }

  @Override public boolean isZero() {
    return digits.size() == 1 && digits.getFirst() == 0;
  }

  private int convertHexToInt(char ch) {
    if (ch >= '0' && ch <= '9') {
      return ch - '0';
    } else if (ch >= 'a' && ch <= 'f') {
      return ch - 'a' + 10;
    } else if (ch >= 'A' && ch <= 'F') {
      return ch - 'A' + 10;
    }
    return -1;
  }

  private void normalize() {
    while (dp >= digits.size()) {
      digits.addLast(0);
    }
    while (dp < 0) {
      digits.addFirst(0);
      dp++;
    }
    while (digits.getFirst() == 0 && dp > 0) {
      digits.removeFirst();
      dp--;
    }
    while (digits.getLast() == 0 && dp < digits.size() - 1) {
      digits.removeLast();
    }
  }

  @Override public String toString() {
    StringBuilder sb = new StringBuilder();
    Iterator<Integer> iter = digits.descendingIterator();
    for (int i = digits.size() - 1; iter.hasNext() && i >= 0; i--) {
      sb.append(iter.next());
      if (i == dp) sb.append('.');
    }
    return sb.toString();
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BigBinary that = (BigBinary) o;
    return negative == that.negative &&
        dp == that.dp &&
        digits.equals(that.digits);
  }

  @Override public int hashCode() {
    return Objects.hash(digits, negative, dp);
  }
}
