package ch.sbb.atlas.servicepointdirectory.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Schema(enumAsRef = true, example = "RAILNET_POINT")
@Getter
@RequiredArgsConstructor
public enum OperatingPointType implements CodeAndDesignations {

  INVENTORY_POINT(30, false, "Inventarpunkt", "Point d'inventaire", "punto di inventario", "Inventory point", "betriebspunkt", null,
      null, null, null),
  SYSTEM_OPERATING_POINT(40, false, "System Betriebspunkt", "Point d’exploitation système", "Punto d’esercizio sistema",
      "System operating point", "betriebspunkt", null, null, null, null),
  RAILNET_POINT(31, false, "BP Netze", "Point de réseau", "Punto di rete", "Network point", "betriebspunkt", null, null, null, null),

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

  public static OperatingPointType from(Integer id) {
    return Arrays.stream(OperatingPointType.values()).filter(operatingPointType -> Objects.equals(operatingPointType.getId(), id))
        .findFirst().orElse(null);
  }

  @Override
  public String getCode() {
    return String.valueOf(id);
  }
}
