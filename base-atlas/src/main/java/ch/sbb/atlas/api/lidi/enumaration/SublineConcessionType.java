package ch.sbb.atlas.api.lidi.enumaration;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(enumAsRef = true)
public enum SublineConcessionType {

  FEDERALLY_LICENSED_OR_APPROVED_LINE("EK"),
  VARIANT_OF_A_FRANCHISED_LINE("VK"),
  CANTONALLY_APPROVED_LINE("KB"),
  RACK_FREE_TRIPS("RF"),
  RACK_FREE_UNPUBLISHED_LINE("NP"),
  LINE_ABROAD("AL");

  private final String shortName;

  public static SublineConcessionType from(String shortName) {
    return Arrays.stream(SublineConcessionType.values())
        .filter(concessionType -> Objects.equals(concessionType.getShortName(), shortName))
        .findFirst().orElse(null);
  }

}
