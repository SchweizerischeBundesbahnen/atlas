package ch.sbb.workflow.helper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class OtpHelperTest {

  @Test
  void shouldHashSameValueExactlyToSameHash() {
    String pinCode = "yBb3St3TeaW";
    String hashedPinCode = OtpHelper.hashPinCode(pinCode);
    String hashedPinCodeAgain = OtpHelper.hashPinCode(pinCode);

    assertThat(hashedPinCode).isEqualTo(hashedPinCodeAgain);
  }
}