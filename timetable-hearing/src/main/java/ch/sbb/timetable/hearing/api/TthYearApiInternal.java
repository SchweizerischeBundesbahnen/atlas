package ch.sbb.timetable.hearing.api;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingYearModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "TTH Year")
public interface TthYearApiInternal {

  String BASE_PATH = "internal/tth/year";

  @PostMapping(BASE_PATH + "/{year}/start")
  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING)")
  TimetableHearingYearModel startTimetableHearingYear(@PathVariable Long year);

  @PostMapping(BASE_PATH + "/{year}/close")
  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastSupervisor(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING)")
  TimetableHearingYearModel closeTimetableHearingYear(@PathVariable Long year);
}
