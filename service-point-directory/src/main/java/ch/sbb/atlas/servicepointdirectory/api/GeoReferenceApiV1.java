package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.servicepoint.GeoReference;
import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.geoupdate.job.model.GeoUpdateItemResultModel;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "GeoData")
@RequestMapping("v1/geodata")
public interface GeoReferenceApiV1 {

  @GetMapping("/reverse-geocode")
  GeoReference getLocationInformation(CoordinatePair coordinatePair);

  @Secured(Role.SECURED_FOR_ATLAS_ADMIN)
  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "update-geo/{sloid}/{id}")
  GeoUpdateItemResultModel updateServicePointGeoLocation(@PathVariable String sloid, @PathVariable Long id);

}
