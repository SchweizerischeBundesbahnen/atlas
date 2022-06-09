package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.SublineVersion;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SublineVersionRepository extends JpaRepository<SublineVersion, Long> {

  default List<SublineVersion> findSwissLineNumberOverlaps(SublineVersion sublineVersion) {
    return findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndSwissSublineNumberIgnoreCase(
        sublineVersion.getValidFrom(), sublineVersion.getValidTo(),
        sublineVersion.getSwissSublineNumber()).stream()
                                               .filter(
                                                   i -> !i.getSlnid()
                                                          .equals(sublineVersion.getSlnid()))
                                               .collect(
                                                   Collectors.toList());
  }

  List<SublineVersion> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndSwissSublineNumberIgnoreCase(
      LocalDate validFrom, LocalDate validTo, String swissNumber);

  List<SublineVersion> findAllBySlnidOrderByValidFrom(String slnid);

  List<SublineVersion> getSublineVersionByMainlineSlnid(String mainlineSlnid);

  @Modifying(clearAutomatically = true)
  @Query("update subline_version v set v.version = (v.version + 1) where v.slnid = :slnid")
  void incrementVersion(@Param("slnid") String slnid);

}
