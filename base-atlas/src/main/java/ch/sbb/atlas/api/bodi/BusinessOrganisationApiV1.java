package ch.sbb.atlas.api.bodi;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.export.enumeration.ExportType;
import ch.sbb.atlas.model.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Business Organisations")
@RequestMapping("v1/business-organisations")
public interface BusinessOrganisationApiV1 {

  @Operation(deprecated = true, summary = "Use /v1/business-organisations/versions instead. Will be removed by 01.03.2026.")
  @GetMapping
  @PageableAsQueryParam
  Container<BusinessOrganisationModel> getAllBusinessOrganisations(
      @Parameter(hidden = true) Pageable pageable,
      @Parameter @RequestParam(required = false) List<String> searchCriteria,
      @Parameter @RequestParam(required = false) List<String> inSboids,
      @Parameter @RequestParam(required = false) @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN) LocalDate validOn,
      @Parameter @RequestParam(required = false) List<Status> statusChoices
  );

  @GetMapping("versions")
  @PageableAsQueryParam
  Container<BusinessOrganisationVersionModel> getBusinessOrganisationVersions(
      @Parameter(hidden = true) Pageable pageable,
      @ParameterObject BusinessOrganisationVersionRequestParams businessOrganisationVersionRequestParams);

  @GetMapping("versions/{sboid}")
  List<BusinessOrganisationVersionModel> getVersions(@PathVariable String sboid);

  @Deprecated(forRemoval = true)
  @Operation(deprecated = true, summary = "Will be removed by 09.10.2025.")
  @PostMapping(value = "/export/full", produces = MediaType.APPLICATION_JSON_VALUE)
  List<URL> exportFullBusinessOrganisationVersions();

  @Deprecated(forRemoval = true)
  @Operation(deprecated = true, summary = "Will be removed by 09.10.2025.")
  @PostMapping(value = "/export/actual", produces = MediaType.APPLICATION_JSON_VALUE)
  List<URL> exportActualBusinessOrganisationVersions();

  @Deprecated(forRemoval = true)
  @Operation(deprecated = true, summary = "Will be removed by 09.10.2025.")
  @PostMapping(value = "/export/timetable-year-change", produces = MediaType.APPLICATION_JSON_VALUE)
  List<URL> exportFutureTimetableBusinessOrganisationVersions();

  @Deprecated(forRemoval = true)
  @GetMapping(value = "/export/download-gz-json/{exportType}")
  @Operation(deprecated = true, summary = "Use Export File Streaming V2 instead. Will be removed by 09.10.2025.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "404", description = "filename myFile not found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  ResponseEntity<InputStreamResource> streamGzipFile(@PathVariable("exportType") ExportType exportType);

  @Deprecated(forRemoval = true)
  @GetMapping(value = "/export/download-json/{exportType}")
  @Operation(deprecated = true, summary = "Use Export File Streaming V2 instead. Will be removed by 09.10.2025.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "404", description = "filename myFile not found", content = @Content(schema =
      @Schema(implementation = ErrorResponse.class)))
  })
  ResponseEntity<InputStreamResource> streamJsonFile(@PathVariable("exportType") ExportType exportType);

}
