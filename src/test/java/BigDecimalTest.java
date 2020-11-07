import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BigDecimalTest {
  @Test
  public void constructor_FortyTwo() {
    assertThat(new BigDecimal("42")).isEqualTo(new BigDecimal(42));
  }

  @Test
  public void constructor_FirstZeroOmitted() {
    assertThat(new BigDecimal(".3333")).isEqualTo(new BigDecimal("0.3333"));
  }

  @Test
  public void constructor_RedundantZeros() {
    assertThat(new BigDecimal("000042.0000")).isEqualTo(new BigDecimal(42));
  }

  @Test
  public void constructor_Positive() {
    assertThat(new BigDecimal("+42")).isEqualTo(new BigDecimal(42));
  }

  @Test
  public void constructor_Negative() {
    assertThat(new BigDecimal("-42")).isEqualTo(new BigDecimal(-42));
  }

  @Test
  public void constructor_DoubleDecimalPoints_ThrowError() {
    assertThatThrownBy(() -> new BigDecimal("1.5.2"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Unexpected decimal point at 3. There are more than one decimal points: 1.5.2");
  }

  @Test
  public void constructor_ENotation_FortyTwo() {
    assertThat(new BigDecimal("0.0042e+4")).isEqualTo(new BigDecimal(42));
  }

  @Test
  public void constructor_ENotation_SpeedOfLight() {
    assertThat(new BigDecimal("2.9979e+8")).isEqualTo(new BigDecimal("299790000"));
  }

  @Test
  public void constructor_ENotation_FineStructureConstant() {
    assertThat(new BigDecimal("7.297352569311e-3")).isEqualTo(new BigDecimal("0.007297352569311"));
  }

  @Test
  public void constructor_LargeE_DoesNotThrowError() {
    new BigDecimal("1e40000000000");
  }

  @Test
  public void constructor_NegativeLargeE_DoesNotThrowError() {
    new BigDecimal("1e-400000000000");
  }

  @Test
  public void isNegative_Positive() {
    assertThat(new BigDecimal(1).isNegative()).isFalse();
  }

  @Test
  public void isNegative_Negative() {
    assertThat(new BigDecimal(-1).isNegative()).isTrue();
  }

  @Test
  public void multiplyByTwo_TwentyOne() {
    var decimal = new BigDecimal(21);
    decimal.multiplyByTwo();
    assertThat(decimal).isEqualTo(new BigDecimal(42));
  }

  @Test
  public void multiplyByTwo_OneAndHalf() {
    var decimal = new BigDecimal("1.5");
    decimal.multiplyByTwo();
    assertThat(decimal).isEqualTo(new BigDecimal(3));
  }

  @Test
  public void multiplyByTwo_TwoThird() {
    var decimal = new BigDecimal("0.666666");
    decimal.multiplyByTwo();
    assertThat(decimal).isEqualTo(new BigDecimal("1.333332"));
  }

  @Test
  public void divideByTwo_Four() {
    var decimal = new BigDecimal(4);
    decimal.divideByTwo();
    assertThat(decimal).isEqualTo(new BigDecimal(2));
  }

  @Test
  public void divideByTwo_Three() {
    var decimal = new BigDecimal("3");
    decimal.divideByTwo();
    assertThat(decimal).isEqualTo(new BigDecimal("1.5"));
  }

  @Test
  public void isLessThanOne_Zero() {
    assertThat(new BigDecimal(0).isLessThanOne()).isTrue();
  }

  @Test
  public void isLessThanOne_Half() {
    assertThat(new BigDecimal("0.5").isLessThanOne()).isTrue();
  }

  @Test
  public void isLessThanOne_One() {
    assertThat(new BigDecimal("1").isLessThanOne()).isFalse();
  }

  @Test
  public void isLessThanOne_OneAndHalf() {
    assertThat(new BigDecimal("1.5").isLessThanOne()).isFalse();
  }

  @Test
  public void isEqualToOrGreaterThanTwo_OneAndHalf() {
    assertThat(new BigDecimal("1.5").isEqualToOrGreaterThanTwo()).isFalse();
  }

  @Test
  public void isEqualToOrGreaterThanTwo_Two() {
    assertThat(new BigDecimal(2).isEqualToOrGreaterThanTwo()).isTrue();
  }

  @Test
  public void discardNumberPart_Zero() {
    var decimal = new BigDecimal(0);
    decimal.discardNumberPart();
    assertThat(decimal).isEqualTo(new BigDecimal(0));
  }

  @Test
  public void discardNumberPart_OneAndHalf() {
    var decimal = new BigDecimal("1.5");
    decimal.discardNumberPart();
    assertThat(decimal).isEqualTo(new BigDecimal("0.5"));
  }

  @Test
  public void discardNumberPart_FortyTwo() {
    var decimal = new BigDecimal(42);
    decimal.discardNumberPart();
    assertThat(decimal).isEqualTo(new BigDecimal(0));
  }

  @Test
  public void equals_Negative() {
    assertThat(new BigDecimal(-42)).isNotEqualTo(new BigDecimal(42));
  }

  @Test
  public void toString_Zero() {
    var decimal = new BigDecimal(0);
    assertThat(decimal.toString()).isEqualTo("0.");
  }

  @Test
  public void toString_OneAndHalf() {
    var decimal = new BigDecimal("1.5");
    assertThat(decimal.toString()).isEqualTo("1.5");
  }
}
