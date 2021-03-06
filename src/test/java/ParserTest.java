import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class ParserTest {
  private final Parser parser = new Parser();

  @Test
  @Parameters(
      {
          // Hex
          "0x1p-100, 7.888609e-31",
          "0x1p100, 1.2676506e+30",

          // Exactly halfway between 1 and the next float32.
          // Round to even (down).
          "1.000000059604644775390625, 1",
          "0x1.000001p0, 1",
          // Slightly lower.
          "1.000000059604644775390624, 1",
          "0x1.0000008p0, 1",
          "0x1.000000fp0, 1",
          // Slightly higher.
          "1.000000059604644775390626, 1.0000001",
          "0x1.000002p0, 1.0000001",
          "0x1.0000018p0, 1.0000001",
          "0x1.0000011p0, 1.0000001",
          // Slightly higher, but you have to read all the way to the end.
          "1.00000005960464477539062500000000000000000000000000000000000000000000000000000000000000000000000000000000000000001, 1.0000001",
          "0x1.0000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001p0, 1.0000001",

          // largest float32: (1<<128) * (1 - 2^-24)
          "340282346638528859811704183484516925440, 3.4028235e+38",
          "-340282346638528859811704183484516925440, -3.4028235e+38",
          "0x.ffffffp128, 3.4028235e+38",
          "-340282346638528859811704183484516925440, -3.4028235e+38",
          "-0x.ffffffp128, -3.4028235e+38",
          // next float32 - too large
          "3.4028236e38, +Infinity",
          "-3.4028236e38, -Infinity",
          "0x1.0p128, +Infinity",
          "-0x1.0p128, -Infinity",
          // the border is 3.40282356779...e+38
          // borderline - okay
          "3.402823567e38, 3.4028235e+38",
          "-3.402823567e38, -3.4028235e+38",
          "0x.ffffff7fp128, 3.4028235e+38",
          "-0x.ffffff7fp128, -3.4028235e+38",
          // borderline - too large
          "3.4028235678e38, +Infinity",
          "-3.4028235678e38, -Infinity",
          "0x.ffffff8p128, +Infinity",
          "-0x.ffffff8p128, -Infinity",

          // Denormals: less than 2^-126
          "1e-38, 1e-38",
          "1e-39, 1e-39",
          "1e-40, 1e-40",
          "1e-41, 1e-41",
          "1e-42, 1e-42",
          "1e-43, 1e-43",
          "1e-44, 1e-44",
          "6e-45, 6e-45", // 4p-149 = 5.6e-45
          "5e-45, 6e-45",

          // Smallest denormal
          "1e-45, 1e-45", // 1p-149 = 1.4e-45
          "2e-45, 1e-45",
          "3e-45, 3e-45",

          // Near denormals and denormals.
          "0x0.89aBcDp-125, 1.2643093e-38",  // 0x0089abcd
          "0x0.8000000p-125, 1.1754944e-38", // 0x00800000
          "0x0.1234560p-125, 1.671814e-39",  // 0x00123456
          "0x0.1234567p-125, 1.671814e-39",  // rounded down
          "0x0.1234568p-125, 1.671814e-39",  // rounded down
          "0x0.1234569p-125, 1.671815e-39",  // rounded up
          "0x0.1234570p-125, 1.671815e-39",  // 0x00123457
          "0x0.0000010p-125, 1e-45",         // 0x00000001
          "0x0.00000081p-125, 1e-45",        // rounded up
          "0x0.0000008p-125, 0",             // rounded down
          "0x0.0000007p-125, 0",             // rounded down

          // 2^92 = 8388608p+69 = 4951760157141521099596496896 (4.9517602e27)
          // is an exact power of two that needs 8 decimal digits to be correctly
          // parsed back.
          // The float32 before is 16777215p+68 = 4.95175986e+27
          // The halfway is 4.951760009. A bad algorithm that thinks the previous
          // float32 is 8388607p+69 will shorten incorrectly to 4.95176e+27.
          "4951760157141521099596496896, 4.9517602e+27",
      }
  )
  public void parseFloat(String text, float expected) {
    var actual = parser.parseFloat(text);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void parseFloat_PositiveZero() {
    var actual = parser.parseFloat("+0");
    assertThat(actual).isEqualTo(0.f);
  }

  @Test
  public void parseFloat_NegativeZero() {
    var actual = parser.parseFloat("-0");
    assertThat(actual).isEqualTo(-0.f);
  }

  @Test
  public void parseFloat_MantissaRoundedUp() {
    var actual = parser.parseFloat("0x1.ffffffp-110");
    var expected = 1.5407439555097887e-33f;
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @Parameters(
      {
          "1, 1",
          "+1, 1",
          "1e23, 1e+23",
          "1E23, 1e+23",
          "100000000000000000000000, 1e+23",
          "1e-100, 1e-100",
          "123456700, 1.234567e+08",
          "99999999999999974834176, 9.999999999999997e+22",
          "100000000000000000000001, 1.0000000000000001e+23",
          "100000000000000008388608, 1.0000000000000001e+23",
          "100000000000000016777215, 1.0000000000000001e+23",
          "100000000000000016777216, 1.0000000000000003e+23",
          "-1, -1",
          "-0.1, -0.1",
          "-0, -0",
          "1e-20, 1e-20",
          "625e-3, 0.625",

          // Hexadecimal floating-point.
          "0x1p0, 1",
          "0x1p1, 2",
          "0x1p-1, 0.5",
          "0x1ep-1, 15",
          "-0x1ep-1, -15",
          "-0x1_ep-1, -15",
          "0x1p-200, 6.223015277861142e-61",
          "0x1p200, 1.6069380442589903e+60",
          "0x1fFe2.p0, 131042",
          "0x1fFe2.P0, 131042",
          "-0x2p3, -16",
          "0x0.fp4, 15",
          "0x0.fp0, 0.9375",

          // zeros
          "0, 0",
          "0e0, 0",
          "-0e0, -0",
          "+0e0, 0",
          "0e-0, 0",
          "-0e-0, -0",
          "+0e-0, 0",
          "0e+0, 0",
          "-0e+0, -0",
          "+0e+0, 0",
          "0e+01234567890123456789, 0",
          "0.00e-01234567890123456789, 0",
          "-0e+01234567890123456789, -0",
          "-0.00e-01234567890123456789, -0",
          "0x0p+01234567890123456789, 0",
          "0x0.00p-01234567890123456789, 0",
          "-0x0p+01234567890123456789, -0",
          "-0x0.00p-01234567890123456789, -0",

          "0e291, 0", // issue 15364
          "0e292, 0", // issue 15364
          "0e347, 0", // issue 15364
          "0e348, 0", // issue 15364
          "-0e291, -0",
          "-0e292, -0",
          "-0e347, -0",
          "-0e348, -0",
          "0x0p126, 0",
          "0x0p127, 0",
          "0x0p128, 0",
          "0x0p129, 0",
          "0x0p130, 0",
          "0x0p1022, 0",
          "0x0p1023, 0",
          "0x0p1024, 0",
          "0x0p1025, 0",
          "0x0p1026, 0",
          "-0x0p126, -0",
          "-0x0p127, -0",
          "-0x0p128, -0",
          "-0x0p129, -0",
          "-0x0p130, -0",
          "-0x0p1022, -0",
          "-0x0p1023, -0",
          "-0x0p1024, -0",
          "-0x0p1025, -0",
          "-0x0p1026, -0",

          // NaNs
          // TODO: Support NaNs
          //"nan, NaN",
          //"NaN, NaN",
          //"NAN, NaN",

          // Infs
          // TODO: Support Infinity
          //"Infinity, +Infinity",
          //"+Infinity, +Infinity",
          //"-Infinity, -Infinity",

          // largest float64
          "1.7976931348623157e308, 1.7976931348623157e+308",
          "-1.7976931348623157e308, -1.7976931348623157e+308",
          "0x1.fffffffffffffp1023, 1.7976931348623157e+308",
          "-0x1.fffffffffffffp1023, -1.7976931348623157e+308",
          "0x1fffffffffffffp+971, 1.7976931348623157e+308",
          "-0x1fffffffffffffp+971, -1.7976931348623157e+308",
          "0x.1fffffffffffffp1027, 1.7976931348623157e+308",
          "-0x.1fffffffffffffp1027, -1.7976931348623157e+308",

          // next float64 - too large
          "1.7976931348623159e308, +Infinity",
          "-1.7976931348623159e308, -Infinity",
          "0x1p1024, +Infinity",
          "-0x1p1024, -Infinity",
          "0x2p1023, +Infinity",
          "-0x2p1023, -Infinity",
          "0x.1p1028, +Infinity",
          "-0x.1p1028, -Infinity",
          "0x.2p1027, +Infinity",
          "-0x.2p1027, -Infinity",

          // the border is ...158079
          // borderline - okay
          "1.7976931348623158e308, 1.7976931348623157e+308",
          "-1.7976931348623158e308, -1.7976931348623157e+308",
          "0x1.fffffffffffff7fffp1023, 1.7976931348623157e+308",
          "-0x1.fffffffffffff7fffp1023, -1.7976931348623157e+308",
          // borderline - too large
          "1.797693134862315808e308, +Infinity",
          "-1.797693134862315808e308, -Infinity",
          "0x1.fffffffffffff8p1023, +Infinity",
          "-0x1.fffffffffffff8p1023, -Infinity",
          "0x1fffffffffffff.8p+971, +Infinity",
          "-0x1fffffffffffff8p+967, -Infinity",
          "0x.1fffffffffffff8p1027, +Infinity",
          "-0x.1fffffffffffff9p1027, -Infinity",

          // a little too large
          "1e308, 1e+308",
          "2e308, +Infinity",
          "1e309, +Infinity",
          "0x1p1025, +Infinity",

          // way too large
          "1e310, +Infinity",
          "-1e310, -Infinity",
          "1e400, +Infinity",
          "-1e400, -Infinity",
          "1e400000, +Infinity",
          "-1e400000, -Infinity",
          "0x1p1030, +Infinity",
          "0x1p2000, +Infinity",
          "0x1p2000000000, +Infinity",
          "-0x1p1030, -Infinity",
          "-0x1p2000, -Infinity",
          "-0x1p2000000000, -Infinity",

          // denormalized
          "1e-305, 1e-305",
          "1e-306, 1e-306",
          "1e-307, 1e-307",
          "1e-308, 1e-308",
          "1e-309, 1e-309",
          "1e-310, 1e-310",
          "1e-322, 1e-322",
          // smallest denormal
          "5e-324, 5e-324",
          "4e-324, 5e-324",
          "3e-324, 5e-324",
          // too small
          "2e-324, 0",
          // way too small
          "1e-350, 0",
          "1e-400000, 0",

          // Near denormals and denormals.
          "0x2.00000000000000p-1010, 1.8227805048890994e-304", // 0x00e0000000000000
          "0x1.fffffffffffff0p-1010, 1.8227805048890992e-304", // 0x00dfffffffffffff
          "0x1.fffffffffffff7p-1010, 1.8227805048890992e-304", // rounded down
          "0x1.fffffffffffff8p-1010, 1.8227805048890994e-304", // rounded up
          "0x1.fffffffffffff9p-1010, 1.8227805048890994e-304", // rounded up

          "0x2.00000000000000p-1022, 4.450147717014403e-308",  // 0x0020000000000000
          "0x1.fffffffffffff0p-1022, 4.4501477170144023e-308", // 0x001fffffffffffff
          "0x1.fffffffffffff7p-1022, 4.4501477170144023e-308", // rounded down
          "0x1.fffffffffffff8p-1022, 4.450147717014403e-308",  // rounded up
          "0x1.fffffffffffff9p-1022, 4.450147717014403e-308",  // rounded up

          "0x1.00000000000000p-1022, 2.2250738585072014e-308", // 0x0010000000000000
          "0x0.fffffffffffff0p-1022, 2.225073858507201e-308",  // 0x000fffffffffffff
          "0x0.ffffffffffffe0p-1022, 2.2250738585072004e-308", // 0x000ffffffffffffe
          "0x0.ffffffffffffe7p-1022, 2.2250738585072004e-308", // rounded down
          "0x1.ffffffffffffe8p-1023, 2.225073858507201e-308",  // rounded up
          "0x1.ffffffffffffe9p-1023, 2.225073858507201e-308",  // rounded up

          "0x0.00000003fffff0p-1022, 2.072261e-317",  // 0x00000000003fffff
          "0x0.00000003456780p-1022, 1.694649e-317",  // 0x0000000000345678
          "0x0.00000003456787p-1022, 1.694649e-317",  // rounded down
          "0x0.00000003456788p-1022, 1.694649e-317",  // rounded down (half to even)
          "0x0.00000003456790p-1022, 1.6946496e-317", // 0x0000000000345679
          "0x0.00000003456789p-1022, 1.6946496e-317", // rounded up

          "0x0.0000000345678800000000000000000000000001p-1022, 1.6946496e-317", // rounded up

          "0x0.000000000000f0p-1022, 7.4e-323", // 0x000000000000000f
          "0x0.00000000000060p-1022, 3e-323",   // 0x0000000000000006
          "0x0.00000000000058p-1022, 3e-323",   // rounded up
          "0x0.00000000000057p-1022, 2.5e-323", // rounded down
          "0x0.00000000000050p-1022, 2.5e-323", // 0x0000000000000005

          "0x0.00000000000010p-1022, 5e-324",  // 0x0000000000000001
          "0x0.000000000000081p-1022, 5e-324", // rounded up
          "0x0.00000000000008p-1022, 0",       // rounded down
          "0x0.00000000000007fp-1022, 0",      // rounded down

          // try to overflow exponent
          "1e-4294967296, 0",
          "1e+4294967296, +Infinity",
          "1e-18446744073709551616, 0",
          "1e+18446744073709551616, +Infinity",
          "0x1p-4294967296, 0",
          "0x1p+4294967296, +Infinity",
          "0x1p-18446744073709551616, 0",
          "0x1p+18446744073709551616, +Infinity",

          // Parse errors
          "0x1p+2, 4",
          "0x.1p+2, 0.25",
          "0x1p-2, 0.25",
          "0x.1p-2, 0.015625",

          // https://www.exploringbinary.com/java-hangs-when-converting-2-2250738585072012e-308/
          "2.2250738585072012e-308, 2.2250738585072014e-308",
          // https://www.exploringbinary.com/php-hangs-on-numeric-value-2-2250738585072011e-308/
          "2.2250738585072011e-308, 2.225073858507201e-308",

          // A very large number (initially wrongly parsed by the fast algorithm).
          "4.630813248087435e+307, 4.630813248087435e+307",

          // A different kind of very large number.
          "22.222222222222222, 22.22222222222222",
          "2.222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222e+1, 22.22222222222222",
          "0x1.1111111111111p222, 7.18931911124017e+66",
          "0x2.2222222222222p221, 7.18931911124017e+66",
          "0x2.222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222222p221, 7.18931911124017e+66",

          // Exactly halfway between 1 and math.Nextafter(1, 2).
          // Round to even (down).
          "1.00000000000000011102230246251565404236316680908203125, 1",
          "0x1.00000000000008p0, 1",
          // Slightly lower; still round down.
          "1.00000000000000011102230246251565404236316680908203124, 1",
          "0x1.00000000000007Fp0, 1",
          // Slightly higher; round up.
          "1.00000000000000011102230246251565404236316680908203126, 1.0000000000000002",
          "0x1.000000000000081p0, 1.0000000000000002",
          "0x1.00000000000009p0, 1.0000000000000002",
          // Slightly higher, but you have to read all the way to the end.
          "1.0000000000000001110223024625156540423631668090820312500000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001, 1.0000000000000002",
          "0x1.000000000000080000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001p0, 1.0000000000000002",

          // Halfway between x := math.Nextafter(1, 2) and math.Nextafter(x, 2)
          // Round to even (up).
          "1.00000000000000033306690738754696212708950042724609375, 1.0000000000000004",
          "0x1.00000000000018p0, 1.0000000000000004",

          // Underscores.
          "1_23.50_0_0e+1_2, 1.235e+14",

          "0x_1_2.3_4_5p+1_2, 74565",
      }
  )
  public void parseDouble(String text, double expected) {
    var actual = parser.parseDouble(text);
    assertThat(actual).isEqualTo(expected);
  }

  // http://practical-scheme.net/wiliki/wiliki.cgi?Gauche%3A%E6%B5%AE%E5%8B%95%E5%B0%8F%E6%95%B0%E7%82%B9%E6%95%B0%E3%82%92%E3%81%A9%E3%81%93%E3%81%BE%E3%81%A7%E8%AA%AD%E3%82%80%E3%81%8B
  @Test
  public void parseDouble_LongFractionalPart() {
    var text = "1.098612288668109691395245236922525704647490557822749451734694333637494293218608966873615754813732088787970029065957865742368004225930519821052801870767277410603162769183381367179373698844360959903742570316795911521145591917750671347054940166775580222203170252946897560690106521505642868138036317373298577782361";
    assertThat(parser.parseDouble(text)).isEqualTo(1.0986122886681098d);
  }
}
