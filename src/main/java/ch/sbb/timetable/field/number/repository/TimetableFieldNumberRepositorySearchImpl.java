package ch.sbb.timetable.field.number.repository;

import ch.sbb.timetable.field.number.entity.TimetableFieldNumber;
import ch.sbb.timetable.field.number.util.TimeTableFieldNumberQueryBuilder;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class TimetableFieldNumberRepositorySearchImpl implements TimetableFieldNumberRepositorySearch {

  @PersistenceContext
  private EntityManager entityManager;
  private final TimeTableFieldNumberQueryBuilder timeTableFieldNumberQueryBuilder;

  public TimetableFieldNumberRepositorySearchImpl(TimeTableFieldNumberQueryBuilder timeTableFieldNumberQueryBuilder) {
    this.timeTableFieldNumberQueryBuilder = timeTableFieldNumberQueryBuilder;
  }

  @Override
  public List<TimetableFieldNumber> searchVersions(List<String> searchStrings, LocalDate validOn, Pageable pageable) {
    return entityManager.createQuery(timeTableFieldNumberQueryBuilder.queryAll(searchStrings, validOn))
        .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
        .setMaxResults(pageable.getPageSize())
        .getResultList();
  }

}
