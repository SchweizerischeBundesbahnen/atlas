package ch.sbb.atlas.api.lidi.enumaration;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum ConcessionTypeV3 {
  FEDERAL_ZONE_CONCESSION,
  CANTONALLY_APPROVED_LINE,
  RIGHT_FREE_LINE,
  FEDERALLY_LICENSED_LINE,
  FEDERALLY_APPROVED_LINE,
  STATE_TREATY
}
