package ch.sbb.line.directory.module.line.repository;

import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface LineVersionV3Repository
    extends JpaRepository<LineVersionV3, Long>, JpaSpecificationExecutor<LineVersionV3> {

  List<LineVersionV3> findAllBySlnidOrderByValidFrom(String slnid);

  List<LineVersionV3> findAllBySlnidIn(Set<String> slnids);

  /**
   * Versions of other, not revoked slnids (lines and members) with the same swissLineNumber whose validity overlaps.
   */
  default List<LineVersionV3> findSwissLineNumberOverlaps(LineVersionV3 version) {
    if (version.getSwissLineNumber() == null) {
      return List.of();
    }
    return findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndSwissLineNumberIgnoreCase(
        version.getValidFrom(), version.getValidTo(), version.getSwissLineNumber()).stream()
        .filter(i -> !i.getSlnid().equals(version.getSlnid()))
        .filter(i -> i.getStatus() != Status.REVOKED)
        .toList();
  }

  List<LineVersionV3> findAllByValidToGreaterThanEqualAndValidFromLessThanEqualAndSwissLineNumberIgnoreCase(
      LocalDate validFrom, LocalDate validTo, String swissLineNumber);

  @Query("select v from line_version_v3 v where v.referencedLineSlnid = :slnid order by v.validFrom, v.slnid")
  List<LineVersionV3> findAllByReferencedLineSlnid(@Param("slnid") String slnid);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Transactional
  @Query("update line_version_v3 v set v.version = (v.version + 1) where v.slnid = :slnid")
  void incrementVersion(@Param("slnid") String slnid);

}
