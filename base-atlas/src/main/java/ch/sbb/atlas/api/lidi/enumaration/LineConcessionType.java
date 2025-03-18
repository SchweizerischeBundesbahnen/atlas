package ch.sbb.atlas.api.lidi.enumaration;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(enumAsRef = true)
public enum LineConcessionType {

  FEDERALLY_LICENSED_OR_APPROVED_LINE("EK"),
  VARIANT_OF_A_LICENSED_LINE("VK"),
  FEDERAL_ZONE_CONCESSION("GK"),
  LINE_OF_A_ZONE_CONCESSION("LG"),
  CANTONALLY_APPROVED_LINE("KB"),
  RIGHT_FREE_LINE("RF"),
  NOT_LICENSED_UNPUBLISHED_LINE("NP"),
  COLLECTION_LINE("SL"),
  LINE_ABROAD("AL");

  private final String shortName;

  public static LineConcessionType from(String shortName) {
    return Arrays.stream(LineConcessionType.values())
        .filter(concessionType -> Objects.equals(concessionType.getShortName(), shortName))
        .findFirst().orElse(null);
  }

}
