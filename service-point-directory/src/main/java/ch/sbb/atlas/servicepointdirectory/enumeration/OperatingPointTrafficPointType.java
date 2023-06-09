package ch.sbb.atlas.servicepointdirectory.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Schema(enumAsRef = true, example = "TARIFF_POINT")
@Getter
@RequiredArgsConstructor
public enum OperatingPointTrafficPointType implements CodeAndDesignations {

  TARIFF_POINT(50, true, "Tarifpunkt", "Point tarifaire", "Punto tariffale", "Tariff point", "verkehrspunkt,tarifstelle", null,
      null,
      null, null),

  ;

  private final Integer id;
  @Accessors(fluent = true)
  private final boolean hasTimetable;
  private final String designationDe;
  private final String designationFr;
  private final String designationIt;
  private final String designationEn;
  private final String allowedFeatures;
  private final String abbreviationDe;
  private final String abbreviationFr;
  private final String abbreviationIt;
  private final String abbreviationEn;

  public static OperatingPointTrafficPointType from(Integer id) {
    return Arrays.stream(OperatingPointTrafficPointType.values()).filter(operatingPointType -> Objects.equals(operatingPointType.getId(), id))
        .findFirst().orElse(null);
  }

  @Override
  public String getCode() {
    return String.valueOf(id);
  }
}
