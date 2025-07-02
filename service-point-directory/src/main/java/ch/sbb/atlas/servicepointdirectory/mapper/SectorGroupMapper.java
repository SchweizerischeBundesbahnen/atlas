package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.api.servicepoint.CreateSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.SectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateSectorGroupVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.SectorGroupVersion;
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
        .build();
  }

  public static ReadSectorGroupVersionModel toReadModel(SectorGroupVersion sectorGroupVersion,
      List<String> sectorSloids) {
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
        .sectorSloids(sectorSloids)
        .build();
  }

  public static SectorGroupVersion toEntity(CreateSectorGroupVersionModel createSectorGroupVersionModel) {
    return SectorGroupVersion.builder()
        .id(createSectorGroupVersionModel.getId())
        .sloid(createSectorGroupVersionModel.getSloid())
        .trafficPointSloid(createSectorGroupVersionModel.getTrafficPointSloid())
        .validFrom(createSectorGroupVersionModel.getValidFrom())
        .validTo(createSectorGroupVersionModel.getValidTo())
        .designation(createSectorGroupVersionModel.getDesignation())
        .length(createSectorGroupVersionModel.getLength())
        .creator(createSectorGroupVersionModel.getCreator())
        .creationDate(createSectorGroupVersionModel.getCreationDate())
        .editor(createSectorGroupVersionModel.getEditor())
        .editionDate(createSectorGroupVersionModel.getEditionDate())
        .version(createSectorGroupVersionModel.getEtagVersion())
        .build();
  }

  public static SectorGroupVersion toEntity(UpdateSectorGroupVersionModel updateSectorGroupVersionModel) {
    return SectorGroupVersion.builder()
        .designation(updateSectorGroupVersionModel.getDesignation())
        .validFrom(updateSectorGroupVersionModel.getValidFrom())
        .validTo(updateSectorGroupVersionModel.getValidTo())
        .designation(updateSectorGroupVersionModel.getDesignation())
        .length(updateSectorGroupVersionModel.getLength())
        .version(updateSectorGroupVersionModel.getEtagVersion())
        .build();
  }

}
