package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.SublineVersion;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SublineVersionRepository extends JpaRepository<SublineVersion, Long> {

  List<SublineVersion> findAllBySwissLineNumber(String swissLineNumber);

  long countAllBySwissLineNumber(String swissLineNumber);

  List<SublineVersion> findAllBySwissLineNumber(Optional<String> swissLineNumber,
      Pageable pageable);

  default boolean hasUniqueSwissSublineNumber(SublineVersion sublineVersion) {
    return findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndSwissSublineNumber(
        sublineVersion.getValidFrom(), sublineVersion.getValidTo(),
        sublineVersion.getSwissSublineNumber()).stream()
                                               .allMatch(
                                                   i -> sublineVersion.getId().equals(i.getId()));
  }

  List<SublineVersion> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndSwissSublineNumber(
      LocalDate validFrom, LocalDate validTo, String swissNumber);
}
