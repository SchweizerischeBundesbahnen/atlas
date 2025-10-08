package ch.sbb.atlas.servicepointdirectory.module.geodata.api;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.geoupdate.job.model.GeoUpdateItemResultModel;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepointdirectory.geodata.protobuf.VectorTile;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface ServicePointGeoDataApiInternal {

  String MEDIA_TYPE_PROTOBUF = "application/x-protobuf";

  @GetMapping(value = "/internal/service-points/geodata/{z}/{x}/{y}.pbf", produces = MEDIA_TYPE_PROTOBUF)
  @Hidden
  VectorTile.Tile getServicePointsGeoData(@PathVariable Integer z, @PathVariable Integer x, @PathVariable Integer y,
      @RequestParam @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN) Optional<LocalDate> validAtDate);

  @GetMapping("/internal/geodata/reverse-geocode")
  GeoReference getLocationInformation(@Valid CoordinatePair coordinatePair);

  @Secured(Role.SECURED_FOR_ATLAS_ADMIN)
  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "/internal/geodata/update-geo/{sloid}/{id}")
  GeoUpdateItemResultModel updateServicePointGeoLocation(@PathVariable String sloid, @PathVariable Long id);
}
