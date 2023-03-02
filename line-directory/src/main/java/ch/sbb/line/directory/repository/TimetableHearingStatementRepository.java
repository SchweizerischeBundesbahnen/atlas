package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.TimetableHearingStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TimetableHearingStatementRepository extends JpaRepository<TimetableHearingStatement, Long>,
    JpaSpecificationExecutor<TimetableHearingStatement> {

}
