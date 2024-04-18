package ch.sbb.atlas.servicepoint.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.Objects;

@Schema(enumAsRef = true)
@Getter
@RequiredArgsConstructor
public enum OperatingPointTrafficPointType {

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

}
