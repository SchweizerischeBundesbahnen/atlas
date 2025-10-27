package ch.sbb.atlas.servicepointdirectory.module.sector.api;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.sector.ReadSectorVersionModel;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Sectors")
@RequestMapping("internal/sectors")
@Validated
public interface SectorApiInternal {

  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).SEPODI)")
  @PostMapping("{sloid}/revoke")
  void revokeSector(@PathVariable String sloid);

  @GetMapping("{trafficPointSloid}/overview")
  @PageableAsQueryParam
  Container<ReadSectorVersionModel> getSectorsOfTrafficPoint(@PathVariable String trafficPointSloid,
      @Parameter(hidden = true) Pageable pageable);

  @GetMapping("actual-date/{trafficPointSloid}")
  List<ReadSectorVersionModel> getSectorsOfTrafficPointValidToday(@PathVariable String trafficPointSloid);
}
