package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.api.servicepoint.sector.SectorVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.UpdateSectorVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.sector.SectorVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SectorMapper {

  public static SectorVersionModel toModel(SectorVersion sectorVersion) {
    return SectorVersionModel.builder()
        .id(sectorVersion.getId())
        .sloid(sectorVersion.getSloid())
        .trafficPointSloid(sectorVersion.getTrafficPointSloid())
        .designation(sectorVersion.getDesignation())
        .validFrom(sectorVersion.getValidFrom())
        .validTo(sectorVersion.getValidTo())
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
        .etagVersion(sectorVersion.getVersion())
        .status(sectorVersion.getStatus())
        .build();
  }

  public static SectorVersion toEntity(SectorVersionModel createSectorVersionModel) {
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
        .status(createSectorVersionModel.getStatus())
        .build();
  }

  public static SectorVersion toEntity(UpdateSectorVersionModel updateSectorVersionModel) {
    return SectorVersion.builder()
        .designation(updateSectorVersionModel.getDesignation())
        .validFrom(updateSectorVersionModel.getValidFrom())
        .validTo(updateSectorVersionModel.getValidTo())
        .designation(updateSectorVersionModel.getDesignation())
        .north(updateSectorVersionModel.getNorth())
        .east(updateSectorVersionModel.getEast())
        .height(updateSectorVersionModel.getHeight())
        .spatialReference(updateSectorVersionModel.getSpatialReference())
        .length(updateSectorVersionModel.getLength())
        .edgeHeight(updateSectorVersionModel.getEdgeHeight())
        .version(updateSectorVersionModel.getEtagVersion())
        .status(updateSectorVersionModel.getStatus())
        .build();
  }
}
