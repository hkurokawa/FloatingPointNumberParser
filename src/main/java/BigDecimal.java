import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Objects;

public class BigDecimal implements BigNumber {
  private final LinkedList<Integer> digits = new LinkedList<>(); // Digits from right to left
  private boolean negative;
  private int dp; // Location of decimal point from right, e.g. dp = 4 for 3.1415

  public BigDecimal(int n) {
    if (n < 0) {
      negative = true;
      n *= -1;
    }
    do {
      digits.add(n % 10);
      n /= 10;
    } while (n > 0);
  }

  public BigDecimal(String s) {
    if (s == null || s.isEmpty()) {
      throw new IllegalArgumentException("Cannot parse an empty string");
    }
    if (s.startsWith("-")) {
      s = s.substring(1);
      negative = true;
    }
    if (s.startsWith("+")) {
      s = s.substring(1);
    }
    dp = -1;
    int exp = 0;
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      if (ch == '.') {
        if (dp >= 0) {
          throw new IllegalArgumentException("Unexpected decimal point at "
              + i + ". There are more than one decimal points: " + s);
        }
        dp = i;
      } else if (ch >= '0' && ch <= '9') {
        digits.add(ch - '0');
      } else if (ch == 'e' || ch == 'E') {
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
    int carry = 0;
    ListIterator<Integer> iter = digits.listIterator();
    while (iter.hasNext()) {
      int n = iter.next() * 2 + carry;
      iter.set(n % 10);
      carry = n / 10;
    }
    if (carry > 0) digits.add(carry);
    normalize();
  }

  @Override public void divideByTwo() {
    Collections.reverse(digits);
    int carry = 0;
    ListIterator<Integer> iter = digits.listIterator();
    while (iter.hasNext()) {
      int n = iter.next() + carry;
      iter.set(n / 2);
      carry = n % 2 * 10;
    }
    if (carry > 0) {
      digits.add(carry / 2);
      dp++;
    }
    Collections.reverse(digits);
    normalize();
  }

  @Override public boolean isLessThanOne() {
    return (dp == digits.size() - 1 && digits.getLast() == 0);
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
    BigDecimal that = (BigDecimal) o;
    if (negative != that.negative) return false;
    if (dp != that.dp) return false;
    return digits.equals(that.digits);
  }

  @Override public int hashCode() {
    return Objects.hash(digits, dp);
  }
}
