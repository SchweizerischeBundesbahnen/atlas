package ch.sbb.timetable.hearing.repository;

import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.timetable.hearing.entity.TthDossierYear;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TthDossierYearRepository extends JpaRepository<TthDossierYear, Long> {

  Optional<TthDossierYear> findTthDossierYearByHearingStatus(HearingStatus hearingStatus);
}
