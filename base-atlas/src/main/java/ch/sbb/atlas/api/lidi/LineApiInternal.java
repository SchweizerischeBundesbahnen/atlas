package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.annotation.AdminOnly;
import ch.sbb.atlas.annotation.UnauthorizedAllowed;
import ch.sbb.atlas.annotation.UnauthorizedAllowed.FurtherLimitations;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.model.Container;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Lines")
@RequestMapping("internal/lines")
public interface LineApiInternal {

  @PostMapping("{slnid}/revoke")
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).LIDI)")
  void revokeLine(@PathVariable String slnid);

  @AdminOnly
  @DeleteMapping("{slnid}")
  void deleteLines(@PathVariable String slnid);

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @PostMapping("/affectedSublines/{id}")
  @Operation(description = "Returns checked Sublines to short")
  AffectedSublinesModel checkAffectedSublines(@PathVariable Long id,
      @RequestBody @Valid UpdateLineVersionModelV2 newVersion
  );

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @GetMapping
  @PageableAsQueryParam
  Container<LineModel> getOverview(@Parameter(hidden = true) Pageable pageable,
      @Valid @ParameterObject LineRequestParams lineRequestParams);

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @GetMapping("{slnid}")
  LineModel getLine(@PathVariable String slnid);

}
