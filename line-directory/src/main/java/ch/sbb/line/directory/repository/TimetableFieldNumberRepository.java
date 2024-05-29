package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.TimetableFieldNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TimetableFieldNumberRepository extends JpaRepository<TimetableFieldNumber, String>,
    JpaSpecificationExecutor<TimetableFieldNumber> {

    boolean existsByTtfnid(String ttfnid);
}
