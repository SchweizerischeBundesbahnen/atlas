package ch.sbb.workflow.otp.helper;

import java.util.UUID;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.digest.DigestUtils;

@UtilityClass
public class OtpHelper {

  /**
   * Canonical lowercase hyphenated UUID v4 as produced by {@link #generatePinCode()}. Applied as input contract on
   * every endpoint that accepts an OTP.
   */
  public static final String OTP_CODE_REGEX =
      "^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";

  public static String generatePinCode() {
    return UUID.randomUUID().toString();
  }

  public static String hashPinCode(String code) {
    return DigestUtils.sha256Hex(code);
  }

}
