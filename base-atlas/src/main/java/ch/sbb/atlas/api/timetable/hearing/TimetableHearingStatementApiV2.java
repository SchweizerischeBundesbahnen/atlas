package ch.sbb.atlas.api.timetable.hearing;

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
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Timetable Hearing Statements")
@RequestMapping("v2/timetable-hearing/statements")
public interface TimetableHearingStatementApiV2 {

//  @GetMapping(path = "{id}")
//  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastExplicitReader(T(ch.sbb.atlas.kafka.model.user.admin"
//      + ".ApplicationType).TIMETABLE_HEARING)")
//  TimetableHearingStatementModel getStatementV2(@PathVariable Long id);

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastWriter(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING, #statement)")
  TimetableHearingStatementModel createStatementV2(
      @RequestPart @Valid TimetableHearingStatementModel statement,
      @RequestPart(required = false) List<MultipartFile> documents);

//  @ResponseStatus(HttpStatus.CREATED)
//  @PostMapping(path = "external", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
//  @RequestBody(content = @Content(encoding = @Encoding(name = "statement", contentType = MediaType.APPLICATION_JSON_VALUE)))
//  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastWriter(T(ch.sbb.atlas.kafka.model.user.admin"
//      + ".ApplicationType).TIMETABLE_HEARING, #statement)")
//  TimetableHearingStatementModel createStatementExternalV2(
//      @RequestPart @Valid TimetableHearingStatementModel statement,
//      @RequestPart(required = false) List<MultipartFile> documents);
//
//  @ResponseStatus(HttpStatus.OK)
//  @PutMapping(path = "{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
//  TimetableHearingStatementModel updateHearingStatementV2(
//      @PathVariable Long id,
//      @RequestPart @Valid TimetableHearingStatementModel statement,
//      @RequestPart(required = false) List<MultipartFile> documents
//  );

}
