package ch.sbb.atlas.api.timetable.hearing;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Timetable Hearing")
@RequestMapping("v1/timetable-hearing/years")
public interface TimetableHearingYearApiV1 {

  @GetMapping
  Container<TimetableHearingYearModel> getHearingYears(@Parameter(hidden = true) Pageable pageable,
      @Parameter @RequestParam(required = false) List<HearingStatus> statusChoices);

  @GetMapping("{year}")
  TimetableHearingYearModel getHearingYear(@PathVariable Long year);

  @PostMapping
  TimetableHearingYearModel createHearingYear(@RequestBody @Valid TimetableHearingYearModel hearingYearModel);

  @PostMapping("start")
  TimetableHearingYearModel startHearingYear(@RequestBody @Valid TimetableHearingYearModel hearingYearModel);

  @PutMapping
  TimetableHearingYearModel updateTimetableHearingSettings(@RequestBody @Valid TimetableHearingYearModel hearingYearModel);

  @PostMapping("close")
  TimetableHearingYearModel closeTimetableHearing(@RequestBody @Valid TimetableHearingYearModel hearingYearModel);
}
