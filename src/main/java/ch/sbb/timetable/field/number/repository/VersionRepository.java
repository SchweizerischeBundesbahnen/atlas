package ch.sbb.timetable.field.number.repository;

import ch.sbb.timetable.field.number.entity.Version;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionRepository extends JpaRepository<Version, Long> {

  @Query(value = "SELECT v FROM timetable_field_number_version v WHERE v.ttfnid = :ttfnid order by v.validFrom asc")
  List<Version> getAllVersionsVersioned(@Param("ttfnid") String ttfnid);

  @Query(value = "select v from timetable_field_number_version v "
      + "where (v.number = :number or v.swissTimetableFieldNumber = :sttfn) "
      + "and (((v.validFrom <= :validFrom and v.validTo >= :validFrom) or "
      + "(v.validFrom <= :validTo and v.validTo >= :validTo)) "
      + "or (v.validFrom > :validFrom and v.validTo < :validTo)) "
      + "and v.ttfnid not like :ttfnid")
  List<Version> getAllByNumberOrSwissTimetableFieldNumberWithValidityOverlap(@Param("number") String number, @Param("sttfn") String sttfn,
      @Param("validFrom") LocalDate validFrom, @Param("validTo") LocalDate validTo, @Param("ttfnid") String ttfnid);
}
