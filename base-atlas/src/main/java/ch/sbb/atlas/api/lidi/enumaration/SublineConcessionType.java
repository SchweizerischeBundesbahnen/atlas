package ch.sbb.atlas.api.lidi.enumaration;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum SublineConcessionType {
  FEDERALLY_LICENSED_OR_APPROVED_LINE,
  VARIANT_OF_A_FRANCHISED_LINE,
  CANTONALLY_APPROVED_LINE,
  RACK_FREE_UNPUBLISHED_LINE,
  LINE_ABROAD

}
