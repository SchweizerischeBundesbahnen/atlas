package ch.sbb.timetable.field.number.repository;

import ch.sbb.timetable.field.number.entity.Version;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionRepository extends JpaRepository<Version, Long> {

  @Query(value = "SELECT v FROM timetable_field_number_version v WHERE v.ttfnid = :ttfnid order by v.validFrom asc")
  List<Version> getAllVersionsVersioned(@Param("ttfnid") String ttfnid);

  List<Version> findAllByTtfnid(String ttfnid);

  @Query(value = "SELECT v from timetable_field_number_version v where (v.swissTimetableFieldNumber = :sttfn or v.number = :number) and v.ttfnid not like :ttfnid")
  List<Version> getAllByNumberOrSwissTimetableFieldNumberWhereTtfnidIsDifferent(@Param("ttfnid") String ttfnid, @Param("number") String number, @Param("sttfn") String sttfn);

  Boolean existsByNumberOrSwissTimetableFieldNumber(String number, String sttfn);
}
