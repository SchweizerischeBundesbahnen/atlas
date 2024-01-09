package ch.sbb.atlas.api.bodi;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.export.enumeration.ExportType;
import ch.sbb.atlas.model.Status;
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
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Business Organisations")
@RequestMapping("v1/business-organisations")
public interface BusinessOrganisationApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<BusinessOrganisationModel> getAllBusinessOrganisations(
      @Parameter(hidden = true) Pageable pageable,
      @Parameter @RequestParam(required = false) List<String> searchCriteria,
      @Parameter @RequestParam(required = false) List<String> inSboids,
      @Parameter @RequestParam(required = false) @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN) Optional<LocalDate> validOn,
      @Parameter @RequestParam(required = false) List<Status> statusChoices);

  @GetMapping("versions")
  @PageableAsQueryParam
  Container<BusinessOrganisationVersionModel> getBusinessOrganisationVersions(
      @Parameter(hidden = true) Pageable pageable,
      @ParameterObject BusinessOrganisationVersionRequestParams businessOrganisationVersionRequestParams);

  @GetMapping("versions/{sboid}")
  List<BusinessOrganisationVersionModel> getVersions(
      @PathVariable String sboid);

  @PostMapping("{sboid}/revoke")
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).BODI)")
  List<BusinessOrganisationVersionModel> revokeBusinessOrganisation(@PathVariable String sboid);

  @PostMapping("versions")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201"),
  })
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).BODI)")
  BusinessOrganisationVersionModel createBusinessOrganisationVersion(
      @RequestBody @Valid BusinessOrganisationVersionModel newVersion);

  @PostMapping("versions/{id}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
  })

  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).BODI)")
  List<BusinessOrganisationVersionModel> updateBusinessOrganisationVersion(
      @PathVariable Long id,
      @RequestBody @Valid BusinessOrganisationVersionModel newVersion);

  @DeleteMapping("{sboid}")
  void deleteBusinessOrganisation(@PathVariable String sboid);

  @Operation(description = "Export all Business Organisations versions as csv, zip and gz file to the ATLAS Amazon S3 Bucket")
  @PostMapping(value = "/export/full", produces = MediaType.APPLICATION_JSON_VALUE)
  List<URL> exportFullBusinessOrganisationVersions();

  @Operation(description = "Export all actual Business Organisations versions as csv, zip and gz file to the ATLAS Amazon S3 "
      + "Bucket")
  @PostMapping(value = "/export/actual", produces = MediaType.APPLICATION_JSON_VALUE)
  List<URL> exportActualBusinessOrganisationVersions();

  @Operation(description = "Export all Business Organisations versions for the current timetable year change as csv, zip and gz "
      + "file to the ATLAS Amazon S3 Bucket")
  @PostMapping(value = "/export/timetable-year-change", produces = MediaType.APPLICATION_JSON_VALUE)
  List<URL> exportFutureTimetableBusinessOrganisationVersions();

  @Secured(Role.SECURED_FOR_ATLAS_ADMIN)
  @PostMapping("/sync-business-organisations")
  @Operation(description = "Write all Business Organisations to kafka again for redistribution")
  void syncBusinessOrganisations();

  @GetMapping(value = "/export/download-gz-json/{exportType}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "404", description = "filename myFile not found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  ResponseEntity<InputStreamResource> streamGzipFile(@PathVariable("exportType") ExportType exportType);

  @GetMapping(value = "/export/download-json/{exportType}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "404", description = "filename myFile not found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  ResponseEntity<InputStreamResource> streamJsonFile(@PathVariable("exportType") ExportType exportType);

}
