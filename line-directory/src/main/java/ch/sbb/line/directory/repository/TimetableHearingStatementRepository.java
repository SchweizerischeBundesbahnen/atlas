package ch.sbb.line.directory.repository;

import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TimetableHearingStatementRepository extends JpaRepository<TimetableHearingStatement, Long>,
    JpaSpecificationExecutor<TimetableHearingStatement> {

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("update timetable_hearing_statement set statementStatus= :statementStatus, justification= :justification where id = :id")
  void updateHearingStatementStatusWithJustification(@Param("id") Long id,
      @Param("statementStatus") StatementStatus statementStatus,
      @Param("justification") String justification);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("update timetable_hearing_statement set statementStatus= :statementStatus where id = :id")
  void updateHearingStatementStatus(@Param("id") Long id, @Param("statementStatus") StatementStatus statementStatus);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("update timetable_hearing_statement set swissCanton= :swissCanton, comment= :comment where id = :id")
  void updateHearingCantonWithComment(@Param("id") Long id, @Param("swissCanton") SwissCanton swissCanton,
      @Param("comment") String comment);

  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("update timetable_hearing_statement set swissCanton= :swissCanton where id = :id")
  void updateHearingCanton(@Param("id") Long id, @Param("swissCanton") SwissCanton swissCanton);

}
