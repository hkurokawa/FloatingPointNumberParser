public class Parser {
  public float parseFloat(String s) {
    BigNumber d = BigNumber.parse(s);
    int sign = d.isNegative() ? 1 : 0;
    if (d.isZero()) {
      return Float.intBitsToFloat(sign << 31);
    }
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
    if (exponent > 127) return d.isNegative() ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
    if (exponent >= -126) {
      // normal
      exponent += 127;
    } else {
      // subnormal
      // shift the number so that it is in 0.xx..xE-126 format
      while (exponent < -126) {
        d.divideByTwo();
        exponent++;
      }
      exponent = 0;
    }
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
        if (mantissa == 0x800000) {
          // The mantissa is out of the range.  The exponent needs to be incremented.
          mantissa = 0;
          exponent++;
          if (exponent > 127) {
            return d.isNegative() ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
          }
        }
      }
    }

    int bits = mantissa | (exponent << 23) | (sign << 31);
    return Float.intBitsToFloat(bits);
  }

  public double parseDouble(String s) {
    BigNumber d = BigNumber.parse(s);
    long sign = d.isNegative() ? 1 : 0;
    if (d.isZero()) {
      return Double.longBitsToDouble(sign << 63);
    }
    long mantissa = 0;
    long exponent = 0;
    while (d.isEqualToOrGreaterThanTwo()) {
      d.divideByTwo();
      exponent++;
    }
    while (d.isLessThanOne()) {
      d.multiplyByTwo();
      exponent--;
    }
    if (exponent > 1023) return d.isNegative() ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
    if (exponent >= -1022) {
      // normal
      exponent += 1023;
    } else {
      // subnormal
      // shift the number so that it is in 0.xx..xE-1022 format
      while (exponent < -1022) {
        d.divideByTwo();
        exponent++;
      }
      exponent = 0;
    }
    d.discardNumberPart();
    for (int i = 51; i >= 0; i--) {
      d.multiplyByTwo();
      if (!d.isLessThanOne()) {
        mantissa |= 1L << i;
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
        if (mantissa == 0x10000000000000L) {
          // The mantissa is out of the range.  The exponent needs to be incremented.
          mantissa = 0;
          exponent++;
          if (exponent >= 2047) {
            return d.isNegative() ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
          }
        }
      }
    }

    long bits = mantissa | (exponent << 52) | (sign << 63);
    return Double.longBitsToDouble(bits);
  }
}
