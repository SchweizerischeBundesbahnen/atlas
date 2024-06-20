package ch.sbb.workflow.helper;

import java.security.SecureRandom;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

@UtilityClass
public class OtpHelper {

  private static final int COUNT = 6;
  private static final int ZERO = 0;

  public static String generatePinCode() {
    return RandomStringUtils.random(COUNT, ZERO, ZERO, false, true, null, new SecureRandom());
  }

  public static String hashPinCode(String code) {
    return DigestUtils.sha256Hex(code);
  }

}
