package ch.sbb.atlas.api.timetable.hearing;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel.Fields;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Timetable Hearing")
@RequestMapping("v1/timetable-hearing/statements")
public interface TimetableHearingStatementApiV1 {

  @GetMapping
  @PageableAsQueryParam
  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastExplicitReader(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING)")
  Container<TimetableHearingStatementModel> getStatements(
      @Parameter(hidden = true) @PageableDefault(sort = {Fields.timetableYear, Fields.id}) Pageable pageable,
      @ParameterObject TimetableHearingStatementRequestParams statementRequestParams);

  @GetMapping(path = "{id}")
  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastExplicitReader(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING)")
  TimetableHearingStatementModel getStatement(@PathVariable Long id);

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastWriter(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING, #statement)")
  TimetableHearingStatementModel createStatement(
      @RequestPart @Valid TimetableHearingStatementModel statement,
      @RequestPart(required = false) List<MultipartFile> documents);

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "external", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @RequestBody(content = @Content(encoding = @Encoding(name = "statement", contentType = MediaType.APPLICATION_JSON_VALUE)))
  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastWriter(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING, #statement)")
  TimetableHearingStatementModel createStatementExternal(
      @RequestPart @Valid TimetableHearingStatementModel statement,
      @RequestPart(required = false) List<MultipartFile> documents);

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastWriter(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING, #statement)")
  TimetableHearingStatementModel updateHearingStatement(
      @PathVariable Long id,
      @RequestPart @Valid TimetableHearingStatementModel statement,
      @RequestPart(required = false) List<MultipartFile> documents
  );

}
