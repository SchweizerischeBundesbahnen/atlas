package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TimetableFieldNumberVersionRepository extends
    JpaRepository<TimetableFieldNumberVersion, Long> {

  @Query(value = "SELECT v FROM timetable_field_number_version v WHERE v.ttfnid = :ttfnid order by v.validFrom asc")
  List<TimetableFieldNumberVersion> getAllVersionsVersioned(@Param("ttfnid") String ttfnid);

  @Query(value = "select v from timetable_field_number_version v "
      + "where (v.number = :number or lower(v.swissTimetableFieldNumber) = :sttfn) "
      + "and (((v.validFrom <= :validFrom and v.validTo >= :validFrom) or "
      + "(v.validFrom <= :validTo and v.validTo >= :validTo)) "
      + "or (v.validFrom > :validFrom and v.validTo < :validTo)) "
      + "and v.ttfnid not like :ttfnid")
  List<TimetableFieldNumberVersion> getAllByNumberOrSwissTimetableFieldNumberWithValidityOverlap(
      @Param("number") String number, @Param("sttfn") String sttfn,
      @Param("validFrom") LocalDate validFrom, @Param("validTo") LocalDate validTo,
      @Param("ttfnid") String ttfnid);

  @Modifying(clearAutomatically = true)
  @Query("update timetable_field_number_version v set v.version = (v.version + 1) where v.ttfnid = :ttfnid")
  void incrementVersion(@Param("ttfnid") String ttfnid);

  @Query("SELECT tv FROM timetable_field_number_version as tv"
      + " ORDER BY tv.ttfnid, tv.validFrom ASC")
  List<TimetableFieldNumberVersion> getFullTimeTableNumberVersions();

  @Query("SELECT tv FROM timetable_field_number_version as tv"
      + " WHERE  :actualDate >= tv.validFrom AND :actualDate <= tv.validTo"
      + " ORDER BY tv.ttfnid, tv.validFrom ASC")
  List<TimetableFieldNumberVersion> getActualTimeTableNumberVersions(
      @Param("actualDate") LocalDate actualDate);

  @Query("SELECT tv FROM timetable_field_number_version as tv"
      + " WHERE  :validAt >= tv.validFrom AND :validAt <= tv.validTo"
      + " AND tv.ttfnid in :ttfnids"
      + " ORDER BY tv.ttfnid, tv.validFrom ASC")
  List<TimetableFieldNumberVersion> getVersionsValidAt(Set<String> ttfnids, LocalDate validAt);

}
