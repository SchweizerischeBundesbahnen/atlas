package ch.sbb.importservice.controller.geo;

import ch.sbb.atlas.configuration.Role;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Service Point Update Geo")
@RequestMapping("v1/service-point-job")
public interface ServicePointUpdateGeoApiV1 {

  @Secured(Role.SECURED_FOR_ATLAS_ADMIN)
  @PostMapping("update-geo")
  void startServicePointUpdateGeoLocation() throws JobExecutionException;

}
