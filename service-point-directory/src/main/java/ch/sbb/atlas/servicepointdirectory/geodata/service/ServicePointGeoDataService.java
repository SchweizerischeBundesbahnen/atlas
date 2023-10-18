package ch.sbb.atlas.servicepointdirectory.geodata.service;

import static ch.sbb.atlas.servicepointdirectory.repository.ServicePointGeolocationRepository.coordinatesBetween;
import static ch.sbb.atlas.servicepointdirectory.repository.ServicePointGeolocationRepository.validAtDate;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeoData;
import ch.sbb.atlas.servicepointdirectory.geodata.mapper.ServicePointGeoDataMapper;
import ch.sbb.atlas.servicepointdirectory.geodata.protobuf.VectorTile.Tile;
import ch.sbb.atlas.servicepointdirectory.geodata.transformer.BoundingBoxTransformer;
import ch.sbb.atlas.servicepointdirectory.geodata.transformer.GeometryTransformer;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointGeolocationRepository;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServicePointGeoDataService {

  private static final String LAYER_NAME = "service-points";

  private final BoundingBoxTransformer boundingBoxTransformer;
  private final GeometryTransformer geometryTransformer;
  private final VectorTileService vectorTileService;
  private final ServicePointGeoDataMapper servicePointGeoDataMapper;
  private final ServicePointGeolocationRepository geolocationRepository;

  private static final double TEN_PERCENT = 0.1;

  public Tile getGeoData(Integer z, Integer x, Integer y, LocalDate validAtDate) {
    log.debug("Calculating Geodata");
    Envelope tileAreaWgs84Exact = boundingBoxTransformer.calculateBoundingBox(z, x, y);

    log.debug("Building Tile {}/{}/{}", z, x, y);
    Envelope tileAreaWgs84WebExact = geometryTransformer.projectArea(SpatialReference.WGS84,
        tileAreaWgs84Exact, SpatialReference.WGS84WEB);

    Envelope geoDataAreaWgs84 = tenPercentEnhancedArea(tileAreaWgs84Exact);

    return vectorTileService.encodeTileLayer(
        LAYER_NAME,
        buildServicePointsForMap(validAtDate, geoDataAreaWgs84),
        tileAreaWgs84WebExact);
  }

  /**
   * We have to load our Points a bit over the tile border, so it does not have any blank spaces on the map
   */
  private static Envelope tenPercentEnhancedArea(Envelope tileAreaWgs84Exact) {
    double tenPercentOfArea =
        Math.abs(tileAreaWgs84Exact.getMaxX() - tileAreaWgs84Exact.getMinX()) * TEN_PERCENT;

    Envelope geoDataAreaWgs84 = tileAreaWgs84Exact.copy();
    geoDataAreaWgs84.expandBy(tenPercentOfArea);
    return geoDataAreaWgs84;
  }

  private List<Point> buildServicePointsForMap(LocalDate validAtDate, Envelope geoDataAreaWgs84) {
    log.debug("Projecting Geodata Areas");
    Map<SpatialReference, Envelope> geoDataAreas = geometryTransformer.getProjectedAreas(
        geoDataAreaWgs84);

    log.debug("Finding service points");
    Set<ServicePointGeoData> servicePoints = getServicePointGeoData(validAtDate, geoDataAreas);

    log.debug("mapping {} service points", servicePoints.size());
    return servicePointGeoDataMapper.mapToWgs84WebGeometry(servicePoints);
  }

  private Set<ServicePointGeoData> getServicePointGeoData(LocalDate validAtDate, Map<SpatialReference, Envelope> geoDataAreas) {
    return new HashSet<>(geolocationRepository
        .findAll(
            validAtDate(validAtDate).and(
                coordinatesBetween(SpatialReference.LV95, geoDataAreas.get(SpatialReference.LV95))
                    .or(coordinatesBetween(SpatialReference.WGS84, geoDataAreas.get(SpatialReference.WGS84)))
            )
        ));
  }

}
