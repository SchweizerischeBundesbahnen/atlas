package ch.sbb.exportservice.util;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MapperUtil {

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN);
  public static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern(
      AtlasApiConstants.DATE_TIME_FORMAT_PATTERN);

  public static String mapStandardAttributeType(StandardAttributeType attributeType) {
    return attributeType != null ? attributeType.toString() : null;
  }

  public static String mapBooleanOptionalAttributeType(BooleanOptionalAttributeType booleanOptionalAttributeType) {
    return booleanOptionalAttributeType != null ? booleanOptionalAttributeType.toString() : null;
  }

  public static List<MeanOfTransport> getMeansOfTransportSorted(Set<MeanOfTransport> meanOfTransports) {
    return meanOfTransports.stream().sorted().toList();
  }

}
