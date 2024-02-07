package ch.sbb.atlas.servicepoint;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class SloidValidation {

  public static final String SLOID_PREFIX = "ch:1:sloid:";

  public static final int EXPECTED_COLONS_SERVICE_POINT = 3;
  public static final int EXPECTED_COLONS_AREA = 4;
  public static final int EXPECTED_COLONS_PLATFORM = 5;

  public static boolean isSloidValid(String sloid, int expectedAmountOfColons) {
    boolean prefixIsCorrect = prefixIsCorrect(sloid);
    boolean amountOfColonsIsCorrect = amountOfColonsIsCorrect(sloid, expectedAmountOfColons);
    boolean isSid4ptConform = isSid4ptConform(sloid);
    return prefixIsCorrect && amountOfColonsIsCorrect && isSid4ptConform;
  }

  public static boolean isSloidValid(String sloid, int expectedAmountOfColons, ServicePointNumber servicePointNumber) {
    boolean isSloidValid = isSloidValid(sloid, expectedAmountOfColons);
    boolean isServicePointNumberCorrect = isServicePointNumberCorrect(sloid, servicePointNumber);
    return isSloidValid && isServicePointNumberCorrect;
  }

  private static boolean isSid4ptConform(String sloid) {
    Pattern pattern = Pattern.compile(AtlasCharacterSetsRegex.SID4PT);
    boolean result = pattern.matcher(sloid).matches();

    if (!result) {
      throw new SloidNotValidException(sloid, "contains SID4PT invalid characters");
    }
    return true;
  }

  private static boolean prefixIsCorrect(String sloid) {
    boolean result = StringUtils.startsWith(sloid, SLOID_PREFIX);

    if (!result) {
      throw new SloidNotValidException(sloid, "did not start with " + SLOID_PREFIX);
    }
    return true;
  }

  private static boolean isServicePointNumberCorrect(String sloid, ServicePointNumber servicePointNumber) {
    String servicePointSloid = ServicePointNumber.calculateSloid(servicePointNumber);
    boolean result = StringUtils.startsWith(sloid, servicePointSloid);

    if (!result) {
      throw new SloidNotValidException(sloid, "did not start with " + servicePointSloid);
    }
    return true;
  }

  private static boolean amountOfColonsIsCorrect(String sloid, int expectedAmountOfColons) {
    int actualColons = StringUtils.countMatches(sloid, ":");
    boolean result = actualColons == expectedAmountOfColons;

    if (!result) {
      throw new SloidNotValidException(sloid, "did not have " + expectedAmountOfColons + " colons as expected");
    }
    return true;
  }

}
