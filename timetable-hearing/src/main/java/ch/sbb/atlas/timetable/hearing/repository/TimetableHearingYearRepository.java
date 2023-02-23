package ch.sbb.atlas.timetable.hearing.repository;

import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingYear;
import ch.sbb.atlas.timetable.hearing.enumeration.HearingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimetableHearingYearRepository extends JpaRepository<TimetableHearingYear, Long> {

  default boolean hearingActive() {
    return existsByHearingStatus(HearingStatus.ACTIVE);
  }

  boolean existsByHearingStatus(HearingStatus hearingStatus);

}
