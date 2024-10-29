package ch.sbb.atlas.api.lidi.enumaration;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum LineConcessionType {
  FEDERALLY_LICENSED_OR_APPROVED_LINE,
  VARIANT_OF_A_FRANCHISED_LINE,
  FEDERAL_TERRITORIAL_CONCESSION,
  LINE_OF_A_TERRITORIAL_CONCESSION,
  CANTONALLY_APPROVED_LINE,
  RACK_FREE_UNPUBLISHED_LINE,
  COLLECTION_LINE,
  LINE_ABROAD

}
