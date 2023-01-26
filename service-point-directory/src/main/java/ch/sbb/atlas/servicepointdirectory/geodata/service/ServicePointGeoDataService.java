package ch.sbb.atlas.servicepointdirectory.geodata.service;

import static ch.sbb.atlas.servicepointdirectory.repository.ServicePointGeolocationRepository.coordinatesBetween;
import static ch.sbb.atlas.servicepointdirectory.repository.ServicePointGeolocationRepository.validAtDate;

import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeoData;
import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.geodata.mapper.ServicePointGeoDataMapper;
import ch.sbb.atlas.servicepointdirectory.geodata.protobuf.VectorTile.Tile;
import ch.sbb.atlas.servicepointdirectory.geodata.transformer.BoundingBoxTransformer;
import ch.sbb.atlas.servicepointdirectory.geodata.transformer.GeometryTransformer;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointGeolocationRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServicePointGeoDataService {

  private final static String LAYER_NAME = "service-points";
  private final BoundingBoxTransformer boundingBoxTransformer;

  private final GeometryTransformer geometryTransformer;

  private final VectorTileService vectorTileService;

  private final ServicePointGeoDataMapper servicePointGeoDataMapper;

  private final ServicePointGeolocationRepository dataRepository;

  private static final double TEN_PERCENT = 0.1;

  public Tile getGeoData(Integer z, Integer x, Integer y, LocalDate validAtDate) {
    log.info("calculating geodata area");
    final Envelope tileAreaWgs84Exact = boundingBoxTransformer.calculateBoundingBox(z, x, y);

    // vector tiles: get data with 10-20% buffer
    final double tenPercentOfArea =
        Math.abs(tileAreaWgs84Exact.getMaxX() - tileAreaWgs84Exact.getMinX()) * TEN_PERCENT;

    final Envelope geoDataAreaWgs84 = tileAreaWgs84Exact.copy();
    geoDataAreaWgs84.expandBy(tenPercentOfArea);

    log.info("projecting geodata area");
    final Map<SpatialReference, Envelope> geoDataAreas = geometryTransformer.getProjectedAreas(
        geoDataAreaWgs84);

    log.info("finding service points");
    final List<ServicePointGeoData> servicePoints = dataRepository
        .findAll(
            validAtDate(validAtDate).and(
                coordinatesBetween(SpatialReference.LV95,
                    geoDataAreas.get(SpatialReference.LV95))
                    .or(coordinatesBetween(SpatialReference.WGS84WEB,
                        geoDataAreas.get(SpatialReference.WGS84WEB)))
                    .or(coordinatesBetween(SpatialReference.WGS84, geoDataAreaWgs84))
                    .or(coordinatesBetween(SpatialReference.LV03,
                        geoDataAreas.get(SpatialReference.LV03)))
            )
        );

    log.info("mapping {} service points", servicePoints.size());
    final List<Point> pointList = servicePointGeoDataMapper.mapToGeometryList(servicePoints);

    log.info("building tile layer {}/{}/{}", z, x, y);
    final Envelope tileAreaWgs84WebExact = geometryTransformer.projectArea(SpatialReference.WGS84,
        tileAreaWgs84Exact, SpatialReference.WGS84WEB);

    return vectorTileService.encodeTileLayer(
        LAYER_NAME,
        pointList,
        tileAreaWgs84WebExact);
  }
}
