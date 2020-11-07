import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BigBinaryTest {
  @Test
  public void constructor_One() {
    assertThat(new BigBinary("0x1")).isEqualTo(new BigBinary(1));
  }

  @Test
  public void constructor_FortyTwo() {
    assertThat(new BigBinary("0x2A")).isEqualTo(new BigBinary(42));
  }

  @Test
  public void constructor_Half() {
    assertThat(new BigBinary("0x.8")).isEqualTo(new BigBinary("0x1.p-1"));
  }

  @Test
  public void constructor_QuarterOfQuarter() {
    assertThat(new BigBinary("0x.1")).isEqualTo(new BigBinary("0x1p-4"));
  }

  @Test
  public void constructor_Minus255() {
    assertThat(new BigBinary("-0xff")).isEqualTo(new BigBinary(-255));
  }

  @Test
  public void constructor_Zero() {
    assertThat(new BigBinary("0x0p1024")).isEqualTo(new BigBinary(0));
  }

  @Test
  public void constructor_LargeP_DoesNotThrowError() {
    new BigBinary("0x1p40000000000");
  }

  @Test
  public void constructor_NegativeLargeP_DoesNotThrowError() {
    new BigBinary("0x1p-400000000000");
  }

  @Test
  public void isNegative_Negative() {
    assertThat(new BigBinary("-0x0").isNegative()).isTrue();
  }

  @Test
  public void isNegative_Positive() {
    assertThat(new BigBinary("0x0").isNegative()).isFalse();
  }

  @Test
  public void multiplyByTwo() {
    var b = new BigBinary("0x0.8");
    b.multiplyByTwo();
    assertThat(b).isEqualTo(new BigBinary(1));
  }

  @Test
  public void divideByTwo() {
    var b = new BigBinary("0x10");
    b.divideByTwo();
    assertThat(b).isEqualTo(new BigBinary(8));
  }

  @Test
  public void isLessThanOne_One() {
    assertThat(new BigBinary(1).isLessThanOne()).isFalse();
  }

  @Test
  public void isLessThanOne_Eight() {
    assertThat(new BigBinary(8).isLessThanOne()).isFalse();
  }

  @Test
  public void isLessThanOne_Half() {
    assertThat(new BigBinary("0x.8").isLessThanOne()).isTrue();
  }

  @Test
  public void isEqualToOrGreaterThanTwo_One() {
    assertThat(new BigBinary(1).isEqualToOrGreaterThanTwo()).isFalse();
  }

  @Test
  public void isEqualToOrGreaterThanTwo_1024() {
    assertThat(new BigBinary("0x1p10").isEqualToOrGreaterThanTwo()).isTrue();
  }

  @Test
  public void isEqualToOrGreaterThanTwo_CloseToTwo() {
    assertThat(new BigBinary("0x1.ffffffffffffffff").isEqualToOrGreaterThanTwo()).isFalse();
  }

  @Test
  public void discardNumberPart_1024AndHalf() {
    var b = new BigBinary("0x1.002p10");
    b.discardNumberPart();
    assertThat(b).isEqualTo(new BigBinary("0x0.8"));
  }

  @Test
  public void discardNumberPart_Quarter() {
    var b = new BigBinary("0x1p-2");
    b.discardNumberPart();
    assertThat(b).isEqualTo(new BigBinary("0x0.4"));
  }

  @Test
  public void isZero() {
    assertThat(new BigBinary("0x0p100").isZero()).isTrue();
  }
}