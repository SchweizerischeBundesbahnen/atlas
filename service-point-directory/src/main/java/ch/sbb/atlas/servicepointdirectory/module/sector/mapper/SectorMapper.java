package ch.sbb.atlas.servicepointdirectory.module.sector.mapper;

import ch.sbb.atlas.api.servicepoint.GeolocationBaseReadModel;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.api.servicepoint.sector.CreateSectorVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.ReadSectorVersionModel;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepointdirectory.module.geodata.mapper.GeolocationMapper;
import ch.sbb.atlas.servicepointdirectory.module.sector.entity.SectorVersion;
import java.util.Map;
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
        .sectorGeolocation(toGeolocation(sectorVersion))
        .length(sectorVersion.getLength())
        .edgeHeight(sectorVersion.getEdgeHeight())
        .status(sectorVersion.getStatus())
        .creator(sectorVersion.getCreator())
        .creationDate(sectorVersion.getCreationDate())
        .editor(sectorVersion.getEditor())
        .editionDate(sectorVersion.getEditionDate())
        .etagVersion(sectorVersion.getVersion())
        .build();
  }

  private static GeolocationBaseReadModel toGeolocation(SectorVersion sectorVersion) {
    if (sectorVersion == null) {
      return null;
    }
    Map<SpatialReference, CoordinatePair> coordinates = GeolocationMapper.getTransformedCoordinates(sectorVersion);
    return GeolocationBaseReadModel.builder()
        .spatialReference(sectorVersion.getSpatialReference())
        .lv95(coordinates.get(SpatialReference.LV95))
        .wgs84(coordinates.get(SpatialReference.WGS84))
        .lv03(coordinates.get(SpatialReference.LV03))
        .height(sectorVersion.getHeight())
        .build();
  }

  public static SectorVersion toEntity(CreateSectorVersionModel createSectorVersionModel) {
    return SectorVersion.builder()
        .id(createSectorVersionModel.getId())
        .trafficPointSloid(createSectorVersionModel.getTrafficPointSloid())
        .designation(createSectorVersionModel.getDesignation())
        .validFrom(createSectorVersionModel.getValidFrom())
        .validTo(createSectorVersionModel.getValidTo())
        .designation(createSectorVersionModel.getDesignation())
        .north(createSectorVersionModel.getSectorGeolocation().getNorth())
        .east(createSectorVersionModel.getSectorGeolocation().getEast())
        .height(createSectorVersionModel.getSectorGeolocation().getHeight())
        .spatialReference(createSectorVersionModel.getSectorGeolocation().getSpatialReference())
        .length(createSectorVersionModel.getLength())
        .edgeHeight(createSectorVersionModel.getEdgeHeight())
        .creator(createSectorVersionModel.getCreator())
        .creationDate(createSectorVersionModel.getCreationDate())
        .editor(createSectorVersionModel.getEditor())
        .editionDate(createSectorVersionModel.getEditionDate())
        .version(createSectorVersionModel.getEtagVersion())
        .build();
  }
}
