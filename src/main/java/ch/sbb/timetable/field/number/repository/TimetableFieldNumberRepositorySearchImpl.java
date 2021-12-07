package ch.sbb.timetable.field.number.repository;

import ch.sbb.timetable.field.number.entity.TimetableFieldNumber;
import ch.sbb.timetable.field.number.util.TimeTableFieldNumberQueryBuilder;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class TimetableFieldNumberRepositorySearchImpl implements TimetableFieldNumberRepositorySearch {

  private final EntityManager entityManager;
  private final TimeTableFieldNumberQueryBuilder timeTableFieldNumberQueryBuilder;

  @Autowired
  public TimetableFieldNumberRepositorySearchImpl(
      TimeTableFieldNumberQueryBuilder timeTableFieldNumberQueryBuilder,
      EntityManager entityManager) {
    this.entityManager = entityManager;
    this.timeTableFieldNumberQueryBuilder = timeTableFieldNumberQueryBuilder;
  }

  @Override
  public Page<TimetableFieldNumber> searchVersions(List<String> searchStrings, LocalDate validOn, Pageable pageable) {
    Predicate searchPredicate = timeTableFieldNumberQueryBuilder.getAllPredicates(searchStrings, validOn);
    CriteriaQuery<TimetableFieldNumber> criteriaQuery = timeTableFieldNumberQueryBuilder.getTimetableFieldNumberSearchQuery(searchPredicate)
        .orderBy(timeTableFieldNumberQueryBuilder.getOrders(pageable));
    CriteriaQuery<Long> countQuery = timeTableFieldNumberQueryBuilder.getTimetableFieldNumberCountQuery(searchPredicate);
    List<TimetableFieldNumber> resultList = entityManager.createQuery(
            criteriaQuery
        )
        .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
        .setMaxResults(pageable.getPageSize())
        .getResultList();
    long count = entityManager.createQuery(countQuery).getSingleResult();
    return new PageImpl<>(resultList, pageable, count);
  }

}
