public interface BigNumber {
  static BigNumber parse(String s) {
    if (s.startsWith("0x") || s.startsWith("-0x")) {
      return new BigBinary(s);
    } else {
      return new BigDecimal(s);
    }
  }

  boolean isNegative();

  void multiplyByTwo();

  void divideByTwo();

  boolean isLessThanOne();

  boolean isEqualToOrGreaterThanTwo();

  void discardNumberPart();

  boolean isZero();
}
