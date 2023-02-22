package ch.sbb.atlas.timetable.hearing.repository;

import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimetableHearingYearRepository extends JpaRepository<TimetableHearingYear, Long> {

}
