public class Parser {
  public float atof(String s) {
    BigNumber d = BigNumber.parse(s);
    int sign = d.isNegative() ? 1 : 0;
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
    // Round to nearest (even)
    if (!d.isZero()) {
      d.multiplyByTwo();
      if (!d.isLessThanOne()) {
        d.discardNumberPart();
        if (d.isZero()) {
          // Just at the middle of the floating point numbers.  Round to even.
          if ((mantissa & 1) == 1) {
            mantissa++;
          }
        } else {
          mantissa++;
        }
      }
    }

    int bits = mantissa | (exponent << 23) | (sign << 31);
    return Float.intBitsToFloat(bits);
  }
}

