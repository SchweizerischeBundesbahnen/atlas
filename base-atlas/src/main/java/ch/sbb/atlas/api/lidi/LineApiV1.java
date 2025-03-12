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
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Lines")
@RequestMapping("v1/lines")
public interface LineApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<LineModel> getLines(@Parameter(hidden = true) Pageable pageable,
      @Valid @ParameterObject LineRequestParams lineRequestParams);

  @GetMapping("{slnid}")
  LineModel getLine(@PathVariable String slnid);

  @PostMapping("{slnid}/revoke")
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).LIDI)")
  void revokeLine(@PathVariable String slnid);

  @DeleteMapping("{slnid}")
  void deleteLines(@PathVariable String slnid);

  /**
   * @deprecated
   */
  @Deprecated(forRemoval = true, since = "2.328.0")
  @GetMapping("versions/{slnid}")
  List<LineVersionModel> getLineVersions(@PathVariable String slnid);

  @PostMapping({"versions/{id}/skip-workflow"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).LIDI)")
  void skipWorkflow(@PathVariable Long id);

  @Operation(description = "Export all line versions as csv and zip file to the ATLAS Amazon S3 Bucket")
  @PostMapping(value = "/export-csv/full", produces = MediaType.APPLICATION_JSON_VALUE)
  List<URL> exportFullLineVersions();

  @Operation(description = "Export all actual line versions as csv and zip file to the ATLAS Amazon S3 Bucket")
  @PostMapping(value = "/export-csv/actual", produces = MediaType.APPLICATION_JSON_VALUE)
  List<URL> exportActualLineVersions();

  @Operation(description = "Export all line versions for the current timetable year change as csv and zip file to the ATLAS "
      + "Amazon S3 Bucket")
  @PostMapping(value = "/export-csv/timetable-year-change", produces = MediaType.APPLICATION_JSON_VALUE)
  List<URL> exportFutureTimetableLineVersions();

  @Operation(description = "Returns all line versions with its related workflow id")
  @GetMapping("/workflows")
  @PageableAsQueryParam
  Container<LineVersionSnapshotModel> getLineVersionSnapshot(
      @Parameter(hidden = true) Pageable pageable,
      @Parameter @RequestParam(required = false) List<String> searchCriteria,
      @Parameter @RequestParam(required = false) @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN) Optional<LocalDate> validOn,
      @Parameter @RequestParam(required = false) List<WorkflowStatus> statusChoices);

  @GetMapping("/workflows/{id}")
  @Operation(description = "Returns a versions with its related workflow id")
  LineVersionSnapshotModel getLineVersionSnapshotById(@PathVariable Long id);

}
