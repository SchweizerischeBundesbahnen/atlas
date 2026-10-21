package ch.sbb.line.directory.module.tth.controller;

import ch.sbb.atlas.api.timetable.hearing.ExternalTimetableHearingStatementCreateModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.line.directory.module.tth.exception.TimetableHearingMovedException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Keeps the moved endpoint in the line-directory OpenAPI spec, marked deprecated. At runtime the gateway route
 * {@code tth-legacy-v2} forwards this path to timetable-hearing, so this controller is only reached by direct calls.
 */
@Deprecated
@RestController
@Tag(name = "Timetable Hearing Statements")
@RequestMapping("v2/timetable-hearing/statements")
public class TimetableHearingStatementControllerV2 {

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(path = "external", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
  @RequestBody(content = @Content(encoding = @Encoding(name = "statement", contentType = MediaType.APPLICATION_JSON_VALUE)))
  @PreAuthorize("@cantonBasedUserAdministrationService"
      + ".isAtLeastWriter(T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).TIMETABLE_HEARING, #statement)")
  public TimetableHearingStatementModelV2 createStatementExternalDeprecated(
      @RequestPart @Valid ExternalTimetableHearingStatementCreateModel statement,
      @RequestPart(required = false) List<MultipartFile> documents) {
    throw new TimetableHearingMovedException();
  }

}
