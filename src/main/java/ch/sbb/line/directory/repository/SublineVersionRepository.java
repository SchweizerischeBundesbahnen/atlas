package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.SublineVersion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SublineVersionRepository extends JpaRepository<SublineVersion, Long> {

  List<SublineVersion> findAllBySwissLineNumber(String swissLineNumber);
  long countAllBySwissLineNumber(String swissLineNumber);
  List<SublineVersion> findAllBySwissLineNumber(Optional<String> swissLineNumber, Pageable pageable);
}
