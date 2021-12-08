package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.SublineVersion;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SublineVersionRepository extends JpaRepository<SublineVersion, Long> {

  default boolean hasUniqueSwissSublineNumber(SublineVersion sublineVersion) {
    return findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndSwissSublineNumber(
        sublineVersion.getValidFrom(), sublineVersion.getValidTo(),
        sublineVersion.getSwissSublineNumber()).stream()
                                               .allMatch(
                                                   i -> sublineVersion.getId().equals(i.getId()));
  }

  List<SublineVersion> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndSwissSublineNumber(
      LocalDate validFrom, LocalDate validTo, String swissNumber);

  List<SublineVersion> findAllBySlnid(String slnid);
}
