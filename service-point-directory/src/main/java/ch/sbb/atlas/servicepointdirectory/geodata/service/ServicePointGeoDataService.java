package ch.sbb.atlas.servicepointdirectory.geodata.service;

import static ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository.coordinatesBetween;
import static ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository.validAtDate;

import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import ch.sbb.atlas.servicepointdirectory.geodata.mapper.ServicePointGeoDataMapper;
import ch.sbb.atlas.servicepointdirectory.geodata.protobuf.VectorTile.Tile;
import ch.sbb.atlas.servicepointdirectory.geodata.transformer.BoundingBoxTransformer;
import ch.sbb.atlas.servicepointdirectory.geodata.transformer.GeometryTransformer;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
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

  private final ServicePointVersionRepository dataRepository;

  public Tile getGeoData(Integer z, Integer x, Integer y, LocalDate validAt) {
    log.info("calculating geodata area");
    final Envelope areaWgs84 = boundingBoxTransformer.calculateBoundingBox(z, x, y);

    log.info("projecting geodata area");
    final Map<SpatialReference, Envelope> projectedAreas = geometryTransformer.getProjectedAreas(
        areaWgs84);

    log.info("finding service points");
    final List<ServicePointVersion> servicePoints = dataRepository
        .findAll(validAtDate(validAt).and(
            coordinatesBetween(SpatialReference.WGS84, areaWgs84)
                .or(coordinatesBetween(SpatialReference.WGS84WEB,
                    projectedAreas.get(SpatialReference.WGS84WEB)))
                .or(coordinatesBetween(SpatialReference.LV95,
                    projectedAreas.get(SpatialReference.LV95)))
                .or(coordinatesBetween(SpatialReference.LV03,
                    projectedAreas.get(SpatialReference.LV03)))));

    log.info("mapping service points");
    final List<Point> pointList = servicePointGeoDataMapper.mapToGeometryList(servicePoints);

    log.info("building tile layer {}/{}/{}", z, x, y);
    final Tile tile = vectorTileService.encodeTileLayer(LAYER_NAME,
        pointList,
        projectedAreas.get(SpatialReference.WGS84WEB));
    log.info("...tile layer created {}/{}/{}", z, x, y);

    return tile;
  }
}
