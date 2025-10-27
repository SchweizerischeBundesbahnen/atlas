package ch.sbb.atlas.servicepointdirectory.module.sectorgroup.mapper;

import ch.sbb.atlas.api.servicepoint.sector.CreateSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.ReadSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.UpdateSectorGroupVersionModel;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.entity.SectorGroupVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SectorGroupMapper {

  public static ReadSectorGroupVersionModel toModel(SectorGroupVersion sectorGroupVersion) {
    return ReadSectorGroupVersionModel.builder()
        .id(sectorGroupVersion.getId())
        .sloid(sectorGroupVersion.getSloid())
        .trafficPointSloid(sectorGroupVersion.getTrafficPointSloid())
        .designation(sectorGroupVersion.getDesignation())
        .validFrom(sectorGroupVersion.getValidFrom())
        .validTo(sectorGroupVersion.getValidTo())
        .length(sectorGroupVersion.getLength())
        .status(sectorGroupVersion.getStatus())
        .creator(sectorGroupVersion.getCreator())
        .creationDate(sectorGroupVersion.getCreationDate())
        .editor(sectorGroupVersion.getEditor())
        .editionDate(sectorGroupVersion.getEditionDate())
        .etagVersion(sectorGroupVersion.getVersion())
        .build();
  }

  public static SectorGroupVersion toEntity(CreateSectorGroupVersionModel createSectorGroupVersionModel) {
    return SectorGroupVersion.builder()
        .id(createSectorGroupVersionModel.getId())
        .trafficPointSloid(createSectorGroupVersionModel.getTrafficPointSloid())
        .validFrom(createSectorGroupVersionModel.getValidFrom())
        .validTo(createSectorGroupVersionModel.getValidTo())
        .designation(createSectorGroupVersionModel.getDesignation())
        .creator(createSectorGroupVersionModel.getCreator())
        .creationDate(createSectorGroupVersionModel.getCreationDate())
        .editor(createSectorGroupVersionModel.getEditor())
        .editionDate(createSectorGroupVersionModel.getEditionDate())
        .version(createSectorGroupVersionModel.getEtagVersion())
        .length(createSectorGroupVersionModel.getLength())
        .build();
  }

  public static SectorGroupVersion toEntity(UpdateSectorGroupVersionModel updateSectorGroupVersionModel) {
    return SectorGroupVersion.builder()
        .designation(updateSectorGroupVersionModel.getDesignation())
        .validFrom(updateSectorGroupVersionModel.getValidFrom())
        .validTo(updateSectorGroupVersionModel.getValidTo())
        .designation(updateSectorGroupVersionModel.getDesignation())
        .version(updateSectorGroupVersionModel.getEtagVersion())
        .length(updateSectorGroupVersionModel.getLength())
        .build();
  }

}
