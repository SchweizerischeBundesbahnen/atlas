package ch.sbb.timetable.field.number.repository;

import ch.sbb.timetable.field.number.entity.TimetableFieldNumber;
import ch.sbb.timetable.field.number.enumaration.Status;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface TimetableFieldNumberRepositorySearch {

  Page<TimetableFieldNumber> searchVersions(Pageable pageable, List<String> searchStrings, LocalDate validOn, List<Status> statusChoices);

}
