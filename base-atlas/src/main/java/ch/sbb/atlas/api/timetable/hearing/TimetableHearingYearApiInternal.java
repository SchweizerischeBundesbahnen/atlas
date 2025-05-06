package ch.sbb.atlas.api.timetable.hearing;

import static ch.sbb.atlas.model.ResponseCodeDescription.ENTITY_ALREADY_UPDATED;
import static ch.sbb.atlas.model.ResponseCodeDescription.NO_ENTITIES_WERE_MODIFIED;
import static ch.sbb.atlas.model.ResponseCodeDescription.VERSIONING_NOT_IMPLEMENTED;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "[INTERNAL] Timetable Hearing Years")
@RequestMapping("internal/timetable-hearing/years")
public interface TimetableHearingYearApiInternal {

  @GetMapping
  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastExplicitReader(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING)")
  List<TimetableHearingYearModel> getHearingYears(
      @Parameter @RequestParam(required = false) List<HearingStatus> statusChoices);

  @GetMapping("{year}")
  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastExplicitReader(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING)")
  TimetableHearingYearModel getHearingYear(@PathVariable Long year);

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING)")
  TimetableHearingYearModel createHearingYear(@RequestBody @Valid TimetableHearingYearModel hearingYearModel);

  @PostMapping("{year}/start")
  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING)")
  TimetableHearingYearModel startHearingYear(@PathVariable Long year);

  @PutMapping("{year}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "412", description = ENTITY_ALREADY_UPDATED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "501", description = VERSIONING_NOT_IMPLEMENTED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "520", description = NO_ENTITIES_WERE_MODIFIED, content =
      @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING)")
  TimetableHearingYearModel updateTimetableHearingSettings(@PathVariable Long year,
      @RequestBody @Valid TimetableHearingYearModel hearingYearModel);

  @PostMapping("{year}/close")
  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING)")
  TimetableHearingYearModel closeTimetableHearing(@PathVariable Long year);
}
