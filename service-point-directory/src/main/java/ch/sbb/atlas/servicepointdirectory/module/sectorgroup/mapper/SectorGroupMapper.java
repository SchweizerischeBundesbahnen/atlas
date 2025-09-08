package ch.sbb.atlas.servicepointdirectory.module.sectorgroup.mapper;

import ch.sbb.atlas.api.servicepoint.sector.CreateSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.ReadSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.SectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.SectorVersionModel;
import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.entity.SectorGroupVersion;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SectorGroupMapper {

  public static SectorGroupVersionModel toModel(SectorGroupVersion sectorGroupVersion) {
    return SectorGroupVersionModel.builder()
        .id(sectorGroupVersion.getId())
        .sloid(sectorGroupVersion.getSloid())
        .trafficPointSloid(sectorGroupVersion.getTrafficPointSloid())
        .designation(sectorGroupVersion.getDesignation())
        .validFrom(sectorGroupVersion.getValidFrom())
        .validTo(sectorGroupVersion.getValidTo())
        .length(sectorGroupVersion.getLength())
        .creator(sectorGroupVersion.getCreator())
        .creationDate(sectorGroupVersion.getCreationDate())
        .editor(sectorGroupVersion.getEditor())
        .editionDate(sectorGroupVersion.getEditionDate())
        .etagVersion(sectorGroupVersion.getVersion())
        .status(sectorGroupVersion.getStatus())
        .build();
  }

  public static ReadSectorGroupVersionModel toReadModel(SectorGroupVersion sectorGroupVersion,
      List<SectorVersionModel> sectors) {
    return ReadSectorGroupVersionModel.builder()
        .id(sectorGroupVersion.getId())
        .sloid(sectorGroupVersion.getSloid())
        .trafficPointSloid(sectorGroupVersion.getTrafficPointSloid())
        .designation(sectorGroupVersion.getDesignation())
        .validFrom(sectorGroupVersion.getValidFrom())
        .validTo(sectorGroupVersion.getValidTo())
        .length(sectorGroupVersion.getLength())
        .creator(sectorGroupVersion.getCreator())
        .creationDate(sectorGroupVersion.getCreationDate())
        .editor(sectorGroupVersion.getEditor())
        .editionDate(sectorGroupVersion.getEditionDate())
        .etagVersion(sectorGroupVersion.getVersion())
        .sectorVersions(sectors)
        .status(sectorGroupVersion.getStatus())
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
        .status(createSectorGroupVersionModel.getStatus())
        .build();
  }

  public static SectorGroupVersion toEntity(SectorGroupVersionModel updateSectorGroupVersionModel) {
    return SectorGroupVersion.builder()
        .designation(updateSectorGroupVersionModel.getDesignation())
        .validFrom(updateSectorGroupVersionModel.getValidFrom())
        .validTo(updateSectorGroupVersionModel.getValidTo())
        .designation(updateSectorGroupVersionModel.getDesignation())
        .version(updateSectorGroupVersionModel.getEtagVersion())
        .status(updateSectorGroupVersionModel.getStatus())
        .build();
  }

}
