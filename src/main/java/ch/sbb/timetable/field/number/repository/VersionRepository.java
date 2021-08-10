package ch.sbb.timetable.field.number.repository;

import ch.sbb.timetable.field.number.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionRepository extends JpaRepository<Version, Long> {

}
