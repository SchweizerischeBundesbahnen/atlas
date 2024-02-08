package ch.sbb.atlas.location;

import static ch.sbb.atlas.api.AtlasFieldLengths.SERVICE_POINT_NUMBER_LENGTH;

import ch.sbb.atlas.model.exception.BadRequestException;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import java.util.List;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SloidHelper {

  private static final int IRRELEVANT_PARTS_TO_SKIP = 3;

  public static ServicePointNumber getServicePointNumber(String sloid) {
    try {
      List<String> identifiers = Stream.of(sloid.split(":")).skip(IRRELEVANT_PARTS_TO_SKIP).toList();
      String servicePointPart = identifiers.get(0);
      int number = Integer.parseInt(servicePointPart);
      if (servicePointPart.length() >= SERVICE_POINT_NUMBER_LENGTH) {
        return ServicePointNumber.ofNumberWithoutCheckDigit(number);
      } else {
        return ServicePointNumber.of(Country.SWITZERLAND, number);
      }
    } catch (Exception e) {
      throw new BadRequestException("Not a valid sloid, could not determine servicePointNumber of sloid=" + sloid);
    }
  }

}
