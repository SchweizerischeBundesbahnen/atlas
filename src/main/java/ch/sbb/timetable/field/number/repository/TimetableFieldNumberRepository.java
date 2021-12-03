package ch.sbb.timetable.field.number.repository;

import ch.sbb.timetable.field.number.entity.TimetableFieldNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimetableFieldNumberRepository extends JpaRepository<TimetableFieldNumber, String>, TimetableFieldNumberRepositorySearch {

}
