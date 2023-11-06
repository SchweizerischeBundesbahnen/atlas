package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.model.ErrorResponse;
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

@Tag(name = "Timetable Field Numbers")
@RequestMapping("v1/field-numbers")
public interface TimetableFieldNumberApiV1 {

  @GetMapping
  @PageableAsQueryParam
  Container<TimetableFieldNumberModel> getOverview(
      @Parameter(hidden = true) Pageable pageable,
      @Parameter @RequestParam(required = false) List<String> searchCriteria,
      @Parameter @RequestParam(required = false) String number,
      @RequestParam(required = false) String businessOrganisation,
      @Parameter @RequestParam(required = false) @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN) LocalDate validOn,
      @Parameter @RequestParam(required = false) List<Status> statusChoices);

  @GetMapping("/versions/{ttfnId}")
  List<TimetableFieldNumberVersionModel> getAllVersionsVersioned(@PathVariable String ttfnId);

  @PostMapping("/{ttfnId}/revoke")
  @PreAuthorize("@businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TTFN)")
  List<TimetableFieldNumberVersionModel> revokeTimetableFieldNumber(@PathVariable String ttfnId);

  @PostMapping("/versions/{id}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "409", description = "Number or SwissTimeTableFieldNumber are already taken", content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "412", description = "Entity has already been updated (etagVersion out of date)", content =
      @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  List<TimetableFieldNumberVersionModel> updateVersionWithVersioning(@PathVariable Long id,
      @RequestBody @Valid TimetableFieldNumberVersionModel newVersion);

  @PostMapping("/versions")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201"),
      @ApiResponse(responseCode = "409", description = "Number or SwissTimeTableFieldNumber are already taken", content =
      @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  TimetableFieldNumberVersionModel createVersion(
      @RequestBody @Valid TimetableFieldNumberVersionModel newVersion);

  @DeleteMapping("/{ttfnid}")
  void deleteVersions(@PathVariable String ttfnid);

  @Operation(description = "Export all Timetable Field Number versions as csv and zip file to the ATLAS Amazon S3 Bucket")
  @PostMapping(path = "/export-csv/full", produces = MediaType.APPLICATION_JSON_VALUE)
  List<URL> exportFullTimetableFieldNumberVersions();

  @Operation(description = "Export all actual Timetable Field Number versions as csv and zip file to the ATLAS Amazon S3 Bucket")
  @PostMapping(path = "/export-csv/actual", produces = MediaType.APPLICATION_JSON_VALUE)
  List<URL> exportActualTimetableFieldNumberVersions();

  @Operation(description = "Export all Timetable Field Number versions for the current timetable year change as csv and zip "
      + "file to the ATLAS Amazon S3 Bucket")
  @PostMapping(path = "/export-csv/timetable-year-change", produces = MediaType.APPLICATION_JSON_VALUE)
  List<URL> exportTimetableYearChangeTimetableFieldNumberVersions();
}
