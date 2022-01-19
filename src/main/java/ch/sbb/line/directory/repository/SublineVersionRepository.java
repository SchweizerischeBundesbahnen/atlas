package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.SublineVersion;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SublineVersionRepository extends JpaRepository<SublineVersion, Long> {

  default boolean hasUniqueSwissSublineNumber(SublineVersion sublineVersion) {
    return findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndSwissSublineNumberIgnoreCase(
        sublineVersion.getValidFrom(), sublineVersion.getValidTo(),
        sublineVersion.getSwissSublineNumber()).stream()
        .allMatch(
            i -> i.getSlnid().equals(sublineVersion.getSlnid()));
  }

  List<SublineVersion> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndSwissSublineNumberIgnoreCase(
      LocalDate validFrom, LocalDate validTo, String swissNumber);

  List<SublineVersion> findAllBySlnidOrderByValidFrom(String slnid);
}
