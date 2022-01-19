package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.LineVersion;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineVersionRepository extends JpaRepository<LineVersion, Long> {

  default List<LineVersion> findSwissLineNumberOverlaps(LineVersion lineVersion) {
    return findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndSwissLineNumberIgnoreCase(
        lineVersion.getValidFrom(), lineVersion.getValidTo(),
        lineVersion.getSwissLineNumber()).stream()
                                         .filter(
                                             i -> !i.getSlnid().equals(lineVersion.getSlnid()))
                                         .collect(
                                             Collectors.toList());
  }

  List<LineVersion> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndSwissLineNumberIgnoreCase(
      LocalDate validFrom, LocalDate validTo, String swissNumber);

  List<LineVersion> findAllBySlnidOrderByValidFrom(String slnid);
}
