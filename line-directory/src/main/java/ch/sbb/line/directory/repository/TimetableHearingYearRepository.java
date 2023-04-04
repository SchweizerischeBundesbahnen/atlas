package ch.sbb.line.directory.repository;

import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.line.directory.entity.TimetableHearingYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TimetableHearingYearRepository extends JpaRepository<TimetableHearingYear, Long>,
    JpaSpecificationExecutor<TimetableHearingYear> {

  default boolean hearingActive() {
    return existsByHearingStatus(HearingStatus.ACTIVE);
  }

  boolean existsByHearingStatus(HearingStatus hearingStatus);

}
