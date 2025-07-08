package ch.sbb.atlas.api.servicepoint.sector.relation;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
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

  @NotNull
  private String sectorGroupSloid;

  @NotNull
  private String sectorSloid;

}
