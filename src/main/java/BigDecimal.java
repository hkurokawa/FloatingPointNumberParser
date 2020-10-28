import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Objects;

public class BigDecimal {
  private final LinkedList<Integer> digits = new LinkedList<>(); // Big endian
  private boolean negative;
  private int dp; // Location of decimal point from right

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
    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);
      if (ch == '.') {
        dp = s.length() - i - 1;
      } else if (ch >= '0' && ch <= '9') {
        digits.add(ch - '0');
      } else {
        throw new IllegalArgumentException("Unexpected char at " + i + ": " + s);
      }
    }
    Collections.reverse(digits);
  }

  public boolean isNegative() {
    return negative;
  }

  public void multiplyByTwo() {
    int carry = 0;
    ListIterator<Integer> iter = digits.listIterator();
    while (iter.hasNext()) {
      int n = iter.next() * 2 + carry;
      iter.set(n % 10);
      carry = n / 10;
    }
    if (carry > 0) digits.add(carry);
    while (digits.getFirst() == 0 && dp > 0) {
      digits.removeFirst();
      dp--;
    }
  }

  public void divideByTwo() {
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
    if (digits.getFirst() == 0 && dp < digits.size() - 1) digits.removeFirst();
    Collections.reverse(digits);
  }

  public boolean isLessThanOne() {
    return (dp == digits.size() - 1 && digits.getLast() == 0);
  }

  public boolean isEqualToOrGreaterThanTwo() {
    if (dp != digits.size() - 1) return true; // >= 10
    return digits.getLast() > 1;
  }

  public void discardNumberPart() {
    while (dp < digits.size() - 1) digits.removeLast();
    digits.set(digits.size() - 1, 0);
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
    if (dp != that.dp) return false;
    return digits.equals(that.digits);
  }

  @Override public int hashCode() {
    return Objects.hash(digits, dp);
  }

  public boolean isZero() {
    return digits.size() == 1 && digits.getFirst() == 0;
  }
}
