public class Parser {
  public float atof(String s) {
    if (s == null || s.length() == 0) {
      throw new IllegalArgumentException("Empty input: [" + s + "]");
    }
    int sign = 0;
    char first = s.charAt(0);
    if (first == '-') {
      sign = 1;
      s = s.substring(1);
    }
    if (first == '+') {
      s = s.substring(1);
    }
    BigDecimal d = new BigDecimal(s);
    int mantissa = 0;
    int exponent = 0;
    while (d.isEqualToOrGreaterThanTwo()) {
      d.divideByTwo();
      exponent++;
    }
    while (d.isLessThanOne()) {
      d.multiplyByTwo();
      exponent--;
    }
    exponent += 127;
    d.discardNumberPart();
    for (int i = 22; i >= 0; i--) {
      d.multiplyByTwo();
      if (!d.isLessThanOne()) {
        mantissa |= 1 << i;
        d.discardNumberPart();
      }
    }
    int bits = mantissa | (exponent << 23) | (sign << 31);
    return Float.intBitsToFloat(bits);
  }
}

