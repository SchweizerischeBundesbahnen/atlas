package ch.sbb.line.directory.repository;

import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.util.TimeTableFieldNumberQueryBuilder;
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
  public Page<TimetableFieldNumber> searchVersions(Pageable pageable, List<String> searchStrings, LocalDate validOn, List<Status> statusChoices) {
    Predicate searchPredicate = timeTableFieldNumberQueryBuilder.getAllPredicates(searchStrings, validOn, statusChoices);
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
