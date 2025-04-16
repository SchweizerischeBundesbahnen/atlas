package ch.sbb.atlas.api.timetable.hearing;

import static ch.sbb.atlas.model.ResponseCodeDescription.ENTITY_ALREADY_UPDATED;
import static ch.sbb.atlas.model.ResponseCodeDescription.NO_ENTITIES_WERE_MODIFIED;
import static ch.sbb.atlas.model.ResponseCodeDescription.VERSIONING_NOT_IMPLEMENTED;

import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel.Fields;
import ch.sbb.atlas.api.timetable.hearing.model.UpdateHearingCantonModel;
import ch.sbb.atlas.api.timetable.hearing.model.UpdateHearingStatementStatusModel;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Timetable Hearing Statements")
@RequestMapping("internal/timetable-hearing/statements")
public interface TimetableHearingStatementApiInternal {

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "/update-statement-status")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "412", description = ENTITY_ALREADY_UPDATED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "501", description = VERSIONING_NOT_IMPLEMENTED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "520", description = NO_ENTITIES_WERE_MODIFIED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  void updateHearingStatementStatus(
      @org.springframework.web.bind.annotation.RequestBody UpdateHearingStatementStatusModel updateHearingStatementStatus);

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "/update-canton")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "412", description = ENTITY_ALREADY_UPDATED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "501", description = VERSIONING_NOT_IMPLEMENTED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "520", description = NO_ENTITIES_WERE_MODIFIED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  void updateHearingCanton(
      @org.springframework.web.bind.annotation.RequestBody UpdateHearingCantonModel updateHearingCantonModel);

  @GetMapping
  @PageableAsQueryParam
  @PreAuthorize("@cantonBasedUserAdministrationService"
      + ".isAtLeastExplicitReader(T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).TIMETABLE_HEARING)")
  Container<TimetableHearingStatementModelV2> getStatements(
      @Parameter(hidden = true) @PageableDefault(sort = {Fields.timetableYear, Fields.id}) Pageable pageable,
      @ParameterObject TimetableHearingStatementRequestParams statementRequestParams);

  @GetMapping(path = "csv/{language}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  @PreAuthorize("@cantonBasedUserAdministrationService"
      + ".isAtLeastExplicitReader(T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).TIMETABLE_HEARING)")
  Resource getStatementsAsCsv(@PathVariable String language,
      @ParameterObject TimetableHearingStatementRequestParams statementRequestParams);

  @GetMapping(path = "{id}")
  @PreAuthorize("@cantonBasedUserAdministrationService"
      + ".isAtLeastExplicitReader(T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).TIMETABLE_HEARING)")
  TimetableHearingStatementModelV2 getStatement(@PathVariable Long id);

  @GetMapping(path = "{id}/previous")
  @PageableAsQueryParam
  @PreAuthorize("@cantonBasedUserAdministrationService"
      + ".isAtLeastExplicitReader(T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).TIMETABLE_HEARING)")
  TimetableHearingStatementAlternatingModel getPreviousStatement(
      @PathVariable Long id,
      @Parameter(hidden = true) @PageableDefault(sort = {Fields.timetableYear, Fields.id}) Pageable pageable,
      @ParameterObject TimetableHearingStatementRequestParams statementRequestParams);

  @GetMapping(path = "{id}/next")
  @PageableAsQueryParam
  @PreAuthorize("@cantonBasedUserAdministrationService"
      + ".isAtLeastExplicitReader(T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).TIMETABLE_HEARING)")
  TimetableHearingStatementAlternatingModel getNextStatement(
      @PathVariable Long id,
      @Parameter(hidden = true) @PageableDefault(sort = {Fields.timetableYear, Fields.id}) Pageable pageable,
      @ParameterObject TimetableHearingStatementRequestParams statementRequestParams);

  @GetMapping(path = "{id}/documents/{filename}", produces = MediaType.APPLICATION_PDF_VALUE)
  @PreAuthorize("@cantonBasedUserAdministrationService"
      + ".isAtLeastExplicitReader(T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).TIMETABLE_HEARING)")
  Resource getStatementDocument(@PathVariable Long id, @PathVariable String filename);

  @DeleteMapping(path = "{id}/documents/{filename}")
  void deleteStatementDocument(@PathVariable Long id, @PathVariable String filename);

  // ATLAS-2634: File-Upload with specific firewall rule. Be aware when changing the path!
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("@cantonBasedUserAdministrationService"
      + ".isAtLeastWriter(T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).TIMETABLE_HEARING, #statement)")
  TimetableHearingStatementModelV2 createStatement(
      @RequestPart @Valid TimetableHearingStatementModelV2 statement,
      @RequestPart(required = false) List<MultipartFile> documents);

  // ATLAS-2634: File-Upload with specific firewall rule. Be aware when changing the path!
  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "412", description = ENTITY_ALREADY_UPDATED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "501", description = VERSIONING_NOT_IMPLEMENTED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "520", description = NO_ENTITIES_WERE_MODIFIED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  TimetableHearingStatementModelV2 updateHearingStatement(
      @PathVariable Long id,
      @RequestPart @Valid TimetableHearingStatementModelV2 statement,
      @RequestPart(required = false) List<MultipartFile> documents
  );

  @GetMapping(path = "responsible-transport-companies/{ttfnid}/{year}")
  List<TransportCompanyModel> getResponsibleTransportCompanies(@PathVariable String ttfnid, @PathVariable Long year);

}
