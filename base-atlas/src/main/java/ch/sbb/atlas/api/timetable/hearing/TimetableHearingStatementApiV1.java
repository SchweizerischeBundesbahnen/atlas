package ch.sbb.atlas.api.timetable.hearing;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Timetable Hearing")
@RequestMapping("v1/timetable-hearing/statements")
public interface TimetableHearingStatementApiV1 {

  @PostMapping
  TimetableHearingStatementModel createStatement(@RequestBody @Valid TimetableHearingStatementModel statement);

  @PutMapping
  TimetableHearingStatementModel updateStatement(@RequestBody @Valid TimetableHearingStatementModel statement);

}
