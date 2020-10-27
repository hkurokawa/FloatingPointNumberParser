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
          "1, 1",
          "+1, 1",
          "-1, -1",
          "1.5, 1.5",
          "22.222222222222222, 22.22222222222222",
          "1.000000059604644775390625, 1.0",
          "1.000000059604644775390624, 1.0",
          "1.000000059604644775390626, 1.0000001",
          "1.000000059604644775390625000000000000000000000000000000000000000000000000001, 1.0000001",
          "340282346638528859811704183484516925440, 3.4028235e+38",
          "-340282346638528859811704183484516925440, -3.4028235e+38",
          "4951760157141521099596496896, 4.9517602e+27"
      }
  )
  public void atof(String text, float expected) {
    var actual = parser.atof(text);
    assertThat(actual).isEqualTo(expected);
  }
}
