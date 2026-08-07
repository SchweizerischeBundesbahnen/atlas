package ch.sbb.timetable.hearing.repository;

import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.timetable.hearing.entity.TimetableHearingStatement;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TimetableHearingStatementRepository extends JpaRepository<TimetableHearingStatement, Long>,
    JpaSpecificationExecutor<TimetableHearingStatement> {

  @Transactional
  void deleteByStatementStatusAndTimetableYear(StatementStatus statementStatus, Long year);

  List<TimetableHearingStatement> findAllByStatementStatusInAndTimetableYear(Collection<StatementStatus> statementStatuses,
      Long year);

  @Transactional
  @Modifying
  @Query("""
      update ch.sbb.timetable.hearing.entity.TimetableHearingStatement tths
      set tths.dossierId = null, tths.dossierContactMail = null, tths.statementStatus = ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus.RECEIVED
      where tths.id in :statementIds""")
  void removeDossierRelationAndSetReceivedFor(Collection<Long> statementIds);
}