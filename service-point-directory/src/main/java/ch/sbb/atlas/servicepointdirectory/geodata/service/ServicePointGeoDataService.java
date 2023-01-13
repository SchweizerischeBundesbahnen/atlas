package ch.sbb.atlas.servicepointdirectory.geodata.service;

import static ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository.coordinatesBetween;

import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.geodata.mapper.ServicePointGeoDataMapper;
import ch.sbb.atlas.servicepointdirectory.geodata.protobuf.VectorTile.Tile;
import ch.sbb.atlas.servicepointdirectory.geodata.transformer.BoundingBoxTransformer;
import ch.sbb.atlas.servicepointdirectory.geodata.transformer.GeometryTransformer;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import java.util.List;
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

  public Tile getGeoData(Integer z, Integer x, Integer y) {
    final Envelope bboxWgs84Web = geometryTransformer
        .projectEnvelopeToWeb(boundingBoxTransformer.calculateBoundingBox(z, x, y));

    log.info("load service points");
    final List<ServicePointVersion> servicePoints = dataRepository.findAll(
        // TODO: add all filter options that we need
        coordinatesBetween(
            bboxWgs84Web.getMinX(),
            bboxWgs84Web.getMinY(),
            bboxWgs84Web.getMaxX(),
            bboxWgs84Web.getMaxY()
        ));

    log.info("map service points");
    final List<Point> pointList = servicePointGeoDataMapper
        .mapToGeometryList(servicePoints
            .stream()
            .filter(ServicePointVersion::hasGeolocation)
            .toList());

    log.info("building tile layer {}/{}/{}", z, x, y);
    final Tile tile = vectorTileService.encodeTileLayer(LAYER_NAME, pointList, bboxWgs84Web);
    log.info("...tile layer created {}/{}/{}", z, x, y);

    return tile;
  }
}
