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
          "+0, 0",
          "-0, -0",
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
          // the border is 3.40282356779...e+38
          // borderline - okay
          "3.402823567e38, 3.4028235e+38",
          "-3.402823567e38, -3.4028235e+38",
          "0x.ffffff7fp128, 3.4028235e+38",
          "-0x.ffffff7fp128, -3.4028235e+38",
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
}
