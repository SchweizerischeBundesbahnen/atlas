package ch.sbb.atlas.api.servicepoint.sector.relation;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class SectorGroupRelationId {

  private String sectorGroupSloid;

  private String sectorSloid;

}
