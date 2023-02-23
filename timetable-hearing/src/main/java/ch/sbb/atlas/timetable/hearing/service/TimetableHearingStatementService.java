package ch.sbb.atlas.timetable.hearing.service;

import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingStatement;
import ch.sbb.atlas.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.timetable.hearing.repository.TimetableHearingStatementRepository;
import ch.sbb.atlas.timetable.hearing.repository.TimetableHearingYearRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TimetableHearingStatementService {

  private final TimetableHearingStatementRepository timetableHearingStatementRepository;
  private final TimetableHearingYearRepository timetableHearingYearRepository;

  public TimetableHearingStatement createHearingStatement() {
    Long year = 2023L;
    if (!timetableHearingYearRepository.existsById(year)) {
      throw new IdNotFoundException(year);
    }
    TimetableHearingStatement statement = TimetableHearingStatement.builder()
        .timetableYear(year)
        .statementStatus(StatementStatus.RECEIVED)
        .email("mike@thebike.com")
        .statement("Ich mag bitte mehr Bös fahren")
        .build();
    return timetableHearingStatementRepository.save(statement);
  }

  public TimetableHearingStatement updateHearingStatement(TimetableHearingStatement statement) {
    Long year = 2024L; // Year may be updated
    if (!timetableHearingYearRepository.existsById(year)) {
      throw new IdNotFoundException(year);
    }
    statement.setTimetableYear(year);
    statement.setStatement("Züge auch mehr fahren");
    return statement;
  }

}
