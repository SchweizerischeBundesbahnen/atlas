package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.LineVersion;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineVersionRepository extends JpaRepository<LineVersion, Long> {

  default boolean hasUniqueSwissLineNumber(LineVersion lineVersion) {
    return findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndSwissLineNumber(
        lineVersion.getValidFrom(), lineVersion.getValidTo(),
        lineVersion.getSwissLineNumber()).stream()
                                         .allMatch(i -> lineVersion.getId().equals(i.getId()));
  }

  List<LineVersion> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndSwissLineNumber(
      LocalDate validFrom, LocalDate validTo, String swissNumber);
}
