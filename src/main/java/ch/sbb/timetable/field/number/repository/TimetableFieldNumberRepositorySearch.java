package ch.sbb.timetable.field.number.repository;

import ch.sbb.timetable.field.number.entity.TimetableFieldNumber;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface TimetableFieldNumberRepositorySearch {

  Page<TimetableFieldNumber> searchVersions(List<String> searchStrings, LocalDate validOn, Pageable pageable);

}
