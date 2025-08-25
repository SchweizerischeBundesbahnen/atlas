package ch.sbb.atlas.servicepointdirectory.module.servicepoint.mapper;

import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.entity.SectorGroupRelation;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.model.SectorGroupRelationId;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SectorGroupRelationMapper {

  public static SectorGroupRelation toEntity(SectorGroupRelationId sectorGroupRelationId) {
    return SectorGroupRelation.builder()
        .sectorGroupRelationId(sectorGroupRelationId)
        .build();
  }

}
