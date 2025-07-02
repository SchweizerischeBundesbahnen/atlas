package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.api.servicepoint.sector.relation.SectorGroupRelationId;
import ch.sbb.atlas.api.servicepoint.sector.relation.SectorGroupRelationModel;
import ch.sbb.atlas.servicepointdirectory.entity.sector.SectorGroupRelation;
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
