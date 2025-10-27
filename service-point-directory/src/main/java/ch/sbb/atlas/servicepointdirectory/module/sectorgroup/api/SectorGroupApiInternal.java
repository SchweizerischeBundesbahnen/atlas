package ch.sbb.atlas.servicepointdirectory.module.sectorgroup.api;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.sector.ReadSectorVersionModel;
import ch.sbb.atlas.api.servicepoint.sector.ReadSectorGroupVersionModel;
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

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Sector Groups")
@RequestMapping("internal/sector-groups")
@Validated
public interface SectorGroupApiInternal {

  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).SEPODI)")
  @PostMapping("{sloid}/revoke")
  void revokeSector(@PathVariable String sloid);

  @GetMapping("{trafficPointSloid}/overview")
  @PageableAsQueryParam
  Container<ReadSectorGroupVersionModel> getSectorGroupsOfTrafficPoint(@PathVariable String trafficPointSloid,
      @Parameter(hidden = true) Pageable pageable);

  @GetMapping("{sectorGroupSloid}/sectors")
  List<ReadSectorVersionModel> getSectorsBySectorGroupSloid(@PathVariable String sectorGroupSloid);
}
