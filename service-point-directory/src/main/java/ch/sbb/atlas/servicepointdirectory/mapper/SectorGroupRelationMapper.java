package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.api.servicepoint.SectorGroupRelationModel;
import ch.sbb.atlas.servicepointdirectory.entity.SectorGroupRelation;
import ch.sbb.atlas.servicepointdirectory.entity.SectorGroupRelationId;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SectorGroupRelationMapper {

  public static SectorGroupRelation toEntity(SectorGroupRelationModel sectorGroupRelationModel) {

    SectorGroupRelationId sectorGroupRelationId = SectorGroupRelationId.builder()
        .sectorGroupSloid(sectorGroupRelationModel.getSectorGroupSloid())
        .sectorSloid(sectorGroupRelationModel.getSectorSloid())
        .build();

    return SectorGroupRelation.builder()
        .sectorGroupRelationId(sectorGroupRelationId)
        .build();
  }

}
