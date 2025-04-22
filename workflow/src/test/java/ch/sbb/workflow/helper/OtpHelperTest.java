package ch.sbb.workflow.helper;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.workflow.otp.helper.OtpHelper;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OtpHelperTest {

  @Test
  void shouldHashSameValueExactlyToSameHash() {
    String pinCode = "yBb3St3TeaW";
    String hashedPinCode = OtpHelper.hashPinCode(pinCode);
    String hashedPinCodeAgain = OtpHelper.hashPinCode(pinCode);

    assertThat(hashedPinCode).isEqualTo(hashedPinCodeAgain);
  }

  @Test
  void shouldGenerateRandomPinCode() {
    Set<String> generatedPinCodes = new HashSet<>();

    for (int i = 0; i < 10; i++) {
      String pinCode = OtpHelper.generatePinCode();
      System.out.println(OtpHelper.hashPinCode(pinCode));

      boolean isUnique = generatedPinCodes.add(pinCode);
      assertThat(isUnique).isTrue();
    }
  }
}