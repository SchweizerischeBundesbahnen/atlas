package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Lines")
@RequestMapping("v1/lines")
public interface LineApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<LineModel> getLines(@Parameter(hidden = true) Pageable pageable,
      @RequestParam(required = false) Optional<String> swissLineNumber,
      @RequestParam(required = false) List<String> searchCriteria,
      @RequestParam(required = false) List<Status> statusRestrictions,
      @RequestParam(required = false) List<LineType> typeRestrictions,
      @RequestParam(required = false) Optional<String> businessOrganisation,
      @RequestParam(required = false) @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN) Optional<LocalDate> validOn);

  @GetMapping("{slnid}")
  LineModel getLine(@PathVariable String slnid);

  @PostMapping("{slnid}/revoke")
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).LIDI)")
  List<LineVersionModel> revokeLine(@PathVariable String slnid);

  @GetMapping("/covered")
  List<LineModel> getCoveredLines();

  @GetMapping("/versions/covered")
  List<LineVersionModel> getCoveredVersionLines();

  @DeleteMapping("{slnid}")
  void deleteLines(@PathVariable String slnid);

  @PostMapping("versions")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201"),
      @ApiResponse(responseCode = "409", description = "Swiss number is not unique in time", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  LineVersionModel createLineVersion(@RequestBody @Valid LineVersionModel newVersion);

  @GetMapping("versions/{slnid}")
  List<LineVersionModel> getLineVersions(@PathVariable String slnid);

  @PostMapping({"versions/{id}"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "409", description = "Swiss number is not unique in time", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "412", description = "Entity has already been updated (etagVersion out of date)", content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "501", description = "Versioning scenario not implemented", content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "520", description = "No entities were modified after versioning execution", content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  List<LineVersionModel> updateLineVersion(@PathVariable Long id,
      @RequestBody @Valid LineVersionModel newVersion);

  @PostMapping({"versions/{id}/skip-workflow"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).LIDI)")
  void skipWorkflow(@PathVariable Long id);

  @GetMapping("line-coverage/{slnid}")
  CoverageModel getLineCoverage(@PathVariable String slnid);

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
