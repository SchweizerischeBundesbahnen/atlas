package ch.sbb.timetable.field.number.repository;

import ch.sbb.timetable.field.number.entity.Version;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionRepository extends JpaRepository<Version, Long> {

  @Query(value = "SELECT v FROM timetable_field_number_version v WHERE v.ttfnid = :ttfnid order by v.validFrom asc")
  List<Version> getAllVersionsVersioned(@Param("ttfnid") String ttfnid);
}
