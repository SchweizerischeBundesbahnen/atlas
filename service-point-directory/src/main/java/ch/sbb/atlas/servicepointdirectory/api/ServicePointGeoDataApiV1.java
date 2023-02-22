package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.servicepointdirectory.geodata.protobuf.VectorTile;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface ServicePointGeoDataApiV1 {

  String MEDIA_TYPE_PROTOBUF = "application/x-protobuf";

  @GetMapping(value = "/v1/service-points/geodata/{z}/{x}/{y}.pbf", produces = MEDIA_TYPE_PROTOBUF)
  VectorTile.Tile getServicePointsGeoData(@PathVariable Integer z, @PathVariable Integer x, @PathVariable Integer y,
      @RequestParam @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN) Optional<LocalDate> validAtDate);
}
