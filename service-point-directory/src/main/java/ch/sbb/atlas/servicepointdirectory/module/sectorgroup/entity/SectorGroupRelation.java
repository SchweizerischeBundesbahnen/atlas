package ch.sbb.atlas.servicepointdirectory.module.sectorgroup.entity;

import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.model.SectorGroupRelationId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@FieldNameConstants
@Builder
@Entity(name = "sector_group_relations")
public class SectorGroupRelation {

  @EmbeddedId
  private SectorGroupRelationId sectorGroupRelationId;

}
