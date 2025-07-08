package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.servicepointdirectory.entity.sector.SectorGroupRelation;
import ch.sbb.atlas.servicepointdirectory.model.SectorGroupRelationId;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SectorGroupRelationMapper {

  public static SectorGroupRelation toEntity(SectorGroupRelationId sectorGroupRelationId) {
    return SectorGroupRelation.builder()
        .sectorGroupRelationId(sectorGroupRelationId)
        .build();
  }

}
