package ch.sbb.workflow.otp.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
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

      boolean isUnique = generatedPinCodes.add(pinCode);
      assertThat(isUnique).isTrue();
    }
  }

  @Test
  void shouldGeneratePinCodeAsCanonicalLowercaseUuidV4() {
    String pinCode = OtpHelper.generatePinCode();

    assertThat(pinCode).hasSize(36).matches(OtpHelper.OTP_CODE_REGEX);
    assertThat(UUID.fromString(pinCode).version()).isEqualTo(4);
  }

  @Test
  void shouldRejectLegacySixDigitPinCode() {
    assertThat(OtpHelper.OTP_CODE_REGEX).doesNotMatch("123456");
  }

  @Test
  void shouldRejectUppercaseOrPaddedUuid() {
    String pinCode = OtpHelper.generatePinCode();

    assertThat(pinCode.toUpperCase()).doesNotMatch(OtpHelper.OTP_CODE_REGEX);
    assertThat(" " + pinCode).doesNotMatch(OtpHelper.OTP_CODE_REGEX);
    assertThat(pinCode + "\n").doesNotMatch(OtpHelper.OTP_CODE_REGEX);
    assertThat(pinCode.replace("-", "")).doesNotMatch(OtpHelper.OTP_CODE_REGEX);
  }
}
