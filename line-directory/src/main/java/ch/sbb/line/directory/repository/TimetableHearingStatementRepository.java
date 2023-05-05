package ch.sbb.line.directory.repository;

import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TimetableHearingStatementRepository extends JpaRepository<TimetableHearingStatement, Long>,
    JpaSpecificationExecutor<TimetableHearingStatement> {

  @Transactional
  void deleteByStatementStatusAndTimetableYear(StatementStatus statementStatus, Long year);

  List<TimetableHearingStatement> findAllByStatementStatusInAndTimetableYear(Collection<StatementStatus> statementStatuses,
      Long year);

}
