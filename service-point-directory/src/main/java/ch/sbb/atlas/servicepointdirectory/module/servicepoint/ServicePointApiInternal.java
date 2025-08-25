package ch.sbb.atlas.servicepointdirectory.module.servicepoint;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointSwissWithGeoLocationModel;
import ch.sbb.atlas.configuration.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Service Points")
@RequestMapping("internal/service-points")
@Validated
public interface ServicePointApiInternal {

  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).SEPODI)")
  @PostMapping("{servicePointNumber}/revoke")
  List<ReadServicePointVersionModel> revokeServicePoint(@PathVariable Integer servicePointNumber);

  @Secured(Role.SECURED_FOR_ATLAS_ADMIN)
  @PostMapping("/sync-service-points")
  @Operation(description = "Write all Service Points to kafka again for redistribution")
  void syncServicePoints();

  @Secured(Role.SECURED_FOR_ATLAS_ADMIN)
  @GetMapping("/actual-swiss-service-point-with-geo")
  List<ServicePointSwissWithGeoLocationModel> getActualServicePointWithGeolocation();
}
