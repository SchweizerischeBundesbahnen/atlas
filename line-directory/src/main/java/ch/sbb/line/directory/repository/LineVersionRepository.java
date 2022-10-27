package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.LineVersion;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

  //  @Query("SELECT lv FROM line_version as lv"
  //      + " JOIN line_version_workflow as lvw "
  //      + " ON lv.id = lvw.lineVersion.id"
  //      + " ORDER BY lv.validFrom ASC")
  List<LineVersion> findAllBySlnidOrderByValidFrom(String slnid);

  @Query("SELECT lv FROM line_version as lv"
      + " JOIN coverage as c "
      + " ON lv.slnid = c.slnid"
      + " WHERE c.modelType = ch.sbb.line.directory.enumaration.ModelType.LINE"
      + " AND c.coverageType = ch.sbb.line.directory.enumaration.CoverageType.COMPLETE"
      + " ORDER BY lv.slnid, lv.validFrom ASC")
  List<LineVersion> getAllCoveredLineVersions();

  @Modifying(clearAutomatically = true)
  @Query("update line_version v set v.version = (v.version + 1) where v.slnid = :slnid")
  void incrementVersion(@Param("slnid") String slnid);

  @Query("SELECT lv FROM line_version as lv"
      + " ORDER BY lv.slnid, lv.validFrom ASC")
  List<LineVersion> getFullLineVersions();

  @Query("SELECT lv FROM line_version as lv"
      + " WHERE  :actualDate >= lv.validFrom AND :actualDate <= lv.validTo"
      + " ORDER BY lv.slnid, lv.validFrom ASC")
  List<LineVersion> getActualLineVersions(@Param("actualDate") LocalDate actualDate);

}
