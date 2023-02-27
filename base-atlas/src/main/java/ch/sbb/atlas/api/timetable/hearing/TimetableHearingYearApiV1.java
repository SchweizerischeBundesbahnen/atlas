package ch.sbb.atlas.api.timetable.hearing;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Timetable Hearing")
@RequestMapping("v1/timetable-hearing/years")
public interface TimetableHearingYearApiV1 {

  @GetMapping
  Container<TimetableHearingYearModel> getHearingYears(@Parameter(hidden = true) Pageable pageable,
      @Parameter @RequestParam(required = false) List<HearingStatus> statusChoices);

  @GetMapping("{year}")
  TimetableHearingYearModel getHearingYear(@PathVariable Long year);

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  TimetableHearingYearModel createHearingYear(@RequestBody @Valid TimetableHearingYearModel hearingYearModel);

  @PostMapping("{year}/start")
  TimetableHearingYearModel startHearingYear(@PathVariable Long year);

  @PutMapping("{year}")
  TimetableHearingYearModel updateTimetableHearingSettings(@PathVariable Long year,
      @RequestBody @Valid TimetableHearingYearModel hearingYearModel);

  @PostMapping("{year}/close")
  TimetableHearingYearModel closeTimetableHearing(@PathVariable Long year);
}
