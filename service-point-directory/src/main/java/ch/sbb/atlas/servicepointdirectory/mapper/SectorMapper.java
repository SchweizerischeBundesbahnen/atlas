package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.api.servicepoint.CreateSectorVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadSectorVersionModel;
import ch.sbb.atlas.api.servicepoint.SectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.SectorVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.SectorVersion;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SectorMapper {

  public static ReadSectorVersionModel toModel(SectorVersion sectorVersion) {
    return ReadSectorVersionModel.builder()
        .id(sectorVersion.getId())
        .sloid(sectorVersion.getSloid())
        .trafficPointSloid(sectorVersion.getTrafficPointSloid())
        .designation(sectorVersion.getDesignation())
        .validFrom(sectorVersion.getValidFrom())
        .validTo(sectorVersion.getValidTo())
        .designation(sectorVersion.getDesignation())
        .north(sectorVersion.getNorth())
        .east(sectorVersion.getEast())
        .height(sectorVersion.getHeight())
        .spatialReference(sectorVersion.getSpatialReference())
        .length(sectorVersion.getLength())
        .edgeHeight(sectorVersion.getEdgeHeight())
        .creator(sectorVersion.getCreator())
        .creationDate(sectorVersion.getCreationDate())
        .editor(sectorVersion.getEditor())
        .editionDate(sectorVersion.getEditionDate())
        .build();
  }

  public static ReadSectorVersionModel toModelWithGroups(SectorVersion sectorVersion,
      List<SectorGroupVersionModel> sectorGroupVersions) {
    return ReadSectorVersionModel.builder()
        .id(sectorVersion.getId())
        .sloid(sectorVersion.getSloid())
        .trafficPointSloid(sectorVersion.getTrafficPointSloid())
        .designation(sectorVersion.getDesignation())
        .validFrom(sectorVersion.getValidFrom())
        .validTo(sectorVersion.getValidTo())
        .designation(sectorVersion.getDesignation())
        .north(sectorVersion.getNorth())
        .east(sectorVersion.getEast())
        .height(sectorVersion.getHeight())
        .spatialReference(sectorVersion.getSpatialReference())
        .length(sectorVersion.getLength())
        .edgeHeight(sectorVersion.getEdgeHeight())
        .creator(sectorVersion.getCreator())
        .creationDate(sectorVersion.getCreationDate())
        .editor(sectorVersion.getEditor())
        .editionDate(sectorVersion.getEditionDate())
        .sectorGroupVersions(sectorGroupVersions)
        .build();
  }

  public static List<SectorVersionModel> toModelFromList(List<SectorVersion> sectorVersions) {
    return sectorVersions.stream()
        .map(SectorMapper::toModel)
        .collect(Collectors.toList());
  }

  public static SectorVersion toEntity(CreateSectorVersionModel createSectorVersionModel) {
    return SectorVersion.builder()
        .id(createSectorVersionModel.getId())
        .sloid(createSectorVersionModel.getSloid())
        .trafficPointSloid(createSectorVersionModel.getTrafficPointSloid())
        .designation(createSectorVersionModel.getDesignation())
        .validFrom(createSectorVersionModel.getValidFrom())
        .validTo(createSectorVersionModel.getValidTo())
        .designation(createSectorVersionModel.getDesignation())
        .north(createSectorVersionModel.getNorth())
        .east(createSectorVersionModel.getEast())
        .height(createSectorVersionModel.getHeight())
        .spatialReference(createSectorVersionModel.getSpatialReference())
        .length(createSectorVersionModel.getLength())
        .edgeHeight(createSectorVersionModel.getEdgeHeight())
        .creator(createSectorVersionModel.getCreator())
        .creationDate(createSectorVersionModel.getCreationDate())
        .editor(createSectorVersionModel.getEditor())
        .editionDate(createSectorVersionModel.getEditionDate())
        //.status(sectorVersion.getStatus)
        //.sectorGroupVersions(sectorVersion.getSectorGroupVersions())
        .build();
  }
}
