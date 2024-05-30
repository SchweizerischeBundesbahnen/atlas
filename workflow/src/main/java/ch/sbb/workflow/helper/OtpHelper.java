package ch.sbb.workflow.helper;

import java.security.SecureRandom;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

@UtilityClass
public class OtpHelper {

  private static final int COUNT = 6;
  private static final int ZERO = 0;

  public static String generateCode(){
    return RandomStringUtils.random(COUNT, ZERO, ZERO, true, true, null, new SecureRandom());
  }

}
