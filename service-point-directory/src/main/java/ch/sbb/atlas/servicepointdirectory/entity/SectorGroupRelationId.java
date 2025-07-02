package ch.sbb.atlas.servicepointdirectory.entity;

import jakarta.persistence.Column;
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

  @Column(name = "sector_group_sloid")
  private String sectorGroupSloid;

  @Column(name = "sector_sloid")
  private String sectorSloid;

}
