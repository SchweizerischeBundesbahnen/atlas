package ch.sbb.atlas.api.lidi.enumaration.v3;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ConcessionTypeV3", enumAsRef = true)
public enum ConcessionType {
  FEDERAL_ZONE_CONCESSION,
  CANTONALLY_APPROVED_LINE,
  RIGHT_FREE_LINE,
  FEDERALLY_LICENSED_LINE,
  FEDERALLY_APPROVED_LINE,
  STATE_TREATY
}
