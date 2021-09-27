package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.SublineVersion;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SublineVersionRepository extends JpaRepository<SublineVersion, Long> {

  Set<SublineVersion> findAllBySwissLineNumber(String swissLineNumber);
}
