package ch.sbb.atlas.timetable.hearing.service;

import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingStatement;
import ch.sbb.atlas.timetable.hearing.model.TimetableHearingStatementSearchRestrictions;
import ch.sbb.atlas.timetable.hearing.repository.TimetableHearingStatementRepository;
import ch.sbb.atlas.timetable.hearing.repository.TimetableHearingYearRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TimetableHearingStatementService {

  private final TimetableHearingStatementRepository timetableHearingStatementRepository;
  private final TimetableHearingYearRepository timetableHearingYearRepository;

  public Page<TimetableHearingStatement> getHearingStatements(TimetableHearingStatementSearchRestrictions searchRestrictions) {
    return timetableHearingStatementRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  public TimetableHearingStatement getStatementById(Long id) {
    return timetableHearingStatementRepository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  public TimetableHearingStatement createHearingStatement(TimetableHearingStatement statement) {
    checkThatTimetableHearingYearExists(statement);

    statement.setStatementStatus(StatementStatus.RECEIVED);
    return timetableHearingStatementRepository.save(statement);
  }

  public TimetableHearingStatement updateHearingStatement(TimetableHearingStatement statement) {
    checkThatTimetableHearingYearExists(statement);

    return timetableHearingStatementRepository.save(statement);
  }

  private void checkThatTimetableHearingYearExists(TimetableHearingStatement statement) {
    if (!timetableHearingYearRepository.existsById(statement.getTimetableYear())) {
      throw new IdNotFoundException(statement.getTimetableYear());
    }
  }

}
