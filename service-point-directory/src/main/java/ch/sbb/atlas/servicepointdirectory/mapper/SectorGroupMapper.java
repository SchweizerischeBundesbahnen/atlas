package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.api.servicepoint.CreateSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.SectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.SectorVersionModel;
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

  public static ReadSectorGroupVersionModel toReadModelWithSectors(SectorGroupVersion g) {
    List<SectorVersionModel> sectors = g.getSectorVersions().stream()
        .map(SectorMapper::toModel)
        .toList();

    return ReadSectorGroupVersionModel.builder()
        .id(g.getId())
        .sloid(g.getSloid())
        .trafficPointSloid(g.getTrafficPointSloid())
        .designation(g.getDesignation())
        .validFrom(g.getValidFrom())
        .validTo(g.getValidTo())
        .length(g.getLength())
        .creator(g.getCreator())
        .creationDate(g.getCreationDate())
        .editor(g.getEditor())
        .editionDate(g.getEditionDate())
        .etagVersion(g.getVersion())
        .sectorVersions(sectors)
        .build();
  }

  public static ReadSectorGroupVersionModel toReadModel(SectorGroupVersion sectorGroupVersion) {
    return ReadSectorGroupVersionModel.builder()
        .id(sectorGroupVersion.getId())
        .sloid(sectorGroupVersion.getSloid())
        .trafficPointSloid(sectorGroupVersion.getTrafficPointSloid())
        .designation(sectorGroupVersion.getDesignation())
        .validFrom(sectorGroupVersion.getValidFrom())
        .validTo(sectorGroupVersion.getValidTo())
        .designation(sectorGroupVersion.getDesignation())
        .length(sectorGroupVersion.getLength())
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
