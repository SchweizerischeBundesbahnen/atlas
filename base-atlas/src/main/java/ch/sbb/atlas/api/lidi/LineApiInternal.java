package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Lines")
@RequestMapping("internal/lines")
public interface LineApiInternal {

  @PostMapping("{slnid}/revoke")
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).LIDI)")
  void revokeLine(@PathVariable String slnid);

  @DeleteMapping("{slnid}")
  void deleteLines(@PathVariable String slnid);

  @PostMapping({"versions/{id}/skip-workflow"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).LIDI)")
  void skipWorkflow(@PathVariable Long id);

  @Operation(description = "Returns all line versions with its related workflow id")
  @GetMapping("/workflows")
  @PageableAsQueryParam
  Container<LineVersionSnapshotModel> getLineVersionSnapshot(
      @Parameter(hidden = true) Pageable pageable,
      @Parameter @RequestParam(required = false) List<String> searchCriteria,
      @Parameter @RequestParam(required = false) @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN) LocalDate validOn,
      @Parameter @RequestParam(required = false) List<WorkflowStatus> statusChoices);

  @GetMapping("/workflows/{id}")
  @Operation(description = "Returns a versions with its related workflow id")
  LineVersionSnapshotModel getLineVersionSnapshotById(@PathVariable Long id);

  @PostMapping("/affectedSublines/{id}")
  @Operation(description = "Returns checked Sublines to short")
  AffectedSublinesModel checkAffectedSublines(@PathVariable Long id,
      @RequestBody @Valid UpdateLineVersionModelV2 newVersion
  );

  @GetMapping
  @PageableAsQueryParam
  Container<LineModel> getOverview(@Parameter(hidden = true) Pageable pageable,
      @Valid @ParameterObject LineRequestParams lineRequestParams);

  @GetMapping("{slnid}")
  LineModel getLine(@PathVariable String slnid);

}
