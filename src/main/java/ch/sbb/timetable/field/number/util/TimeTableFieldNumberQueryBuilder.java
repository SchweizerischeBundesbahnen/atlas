package ch.sbb.timetable.field.number.util;

import ch.sbb.timetable.field.number.entity.TimetableFieldNumber;
import ch.sbb.timetable.field.number.entity.TimetableFieldNumber_;
import ch.sbb.timetable.field.number.enumaration.Status;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TimeTableFieldNumberQueryBuilder {

  private final CriteriaBuilder criteriaBuilder;
  private final CriteriaQuery<TimetableFieldNumber> criteriaQuery;
  private final CriteriaQuery<Long> criteriaQueryCount;
  private final Root<TimetableFieldNumber> timetableFieldNumberRoot;
  private final Root<TimetableFieldNumber> timetableFieldNumberCountRoot;

  @Autowired
  public TimeTableFieldNumberQueryBuilder(EntityManager entityManager) {
    criteriaBuilder = entityManager.getCriteriaBuilder();
    criteriaQuery = criteriaBuilder.createQuery(TimetableFieldNumber.class);
    criteriaQueryCount = criteriaBuilder.createQuery(Long.class);
    timetableFieldNumberRoot = criteriaQuery.from(TimetableFieldNumber.class);
    timetableFieldNumberCountRoot = criteriaQueryCount.from(TimetableFieldNumber.class);
  }

  private Predicate getValidityPredicate(LocalDate validOn) {
    Path<LocalDate> validFrom = timetableFieldNumberRoot.get(TimetableFieldNumber_.validFrom);
    Path<LocalDate> validTo = timetableFieldNumberRoot.get(TimetableFieldNumber_.validTo);
    return validOn != null ? criteriaBuilder.and(
        criteriaBuilder.lessThanOrEqualTo(validFrom, validOn),
        criteriaBuilder.greaterThanOrEqualTo(validTo, validOn)
    )
        : criteriaBuilder.and();
  }

  private Predicate getStatusPredicate(Set<Status> statusSearches) {
    if (statusSearches.isEmpty()) {
      return criteriaBuilder.and();
    }
    Path<Status> status = timetableFieldNumberRoot.get(TimetableFieldNumber_.status);
    return criteriaBuilder.or(statusSearches.stream()
        .map(statusSearch -> criteriaBuilder.equal(status, statusSearch)
        )
        .toArray(Predicate[]::new)
    );
  }

  private Predicate getStringPredicate(Set<String> searchStrings) {
    if (searchStrings.isEmpty()) {
      return criteriaBuilder.and();
    }
    List<Path<String>> stringPaths = List.of(
        timetableFieldNumberRoot.get(TimetableFieldNumber_.swissTimetableFieldNumber),
        timetableFieldNumberRoot.get(TimetableFieldNumber_.name),
        timetableFieldNumberRoot.get(TimetableFieldNumber_.ttfnid)
    );
    Predicate[] stringPredicates = searchStrings.stream().map(searchString -> criteriaBuilder.or(
        stringPaths.stream().map(stringPath -> criteriaBuilder.like(
                criteriaBuilder.lower(stringPath), "%" + searchString.toLowerCase() + "%"))
            .toArray(Predicate[]::new)
    )).toArray(Predicate[]::new);
    return criteriaBuilder.and(stringPredicates);
  }

  private Set<String> getStatusSearches(Set<String> searchStrings) {
    return searchStrings.stream()
        .filter(searchString -> Arrays.stream(Status.values())
            .anyMatch(statusVal -> statusVal.isStatus(searchString.toUpperCase())))
        .collect(Collectors.toSet());
  }

  public List<Order> getOrders(Pageable pageable) {
    return QueryUtils.toOrders(pageable.getSort(), timetableFieldNumberRoot, criteriaBuilder);
  }

  public Predicate getAllPredicates(List<String> searchStrings, LocalDate validOn) {
    Set<String> searchStringsFiltered = new HashSet<>();
    if (searchStrings != null) {
      searchStringsFiltered = searchStrings.stream()
          .filter(string -> !string.isBlank())
          .map(String::toLowerCase)
          .collect(Collectors.toSet());
    }
    Set<String> statusSearches = getStatusSearches(searchStringsFiltered);
    Set<String> stringSearches = new HashSet<>(searchStringsFiltered);
    stringSearches.removeAll(statusSearches);
    Set<Status> statusSet = statusSearches.stream().map(status -> Status.valueOf(status.toUpperCase())).collect(Collectors.toSet());
    return criteriaBuilder.and(
        getStringPredicate(stringSearches),
        getValidityPredicate(validOn),
        getStatusPredicate(statusSet)
    );
  }

  public CriteriaQuery<Long> getTimetableFieldNumberCountQuery(Predicate predicate) {
    return criteriaQueryCount.select(criteriaBuilder.count(timetableFieldNumberCountRoot)).where(predicate);
  }

  public CriteriaQuery<TimetableFieldNumber> getTimetableFieldNumberSearchQuery(Predicate predicate) {
    return criteriaQuery.select(timetableFieldNumberRoot).where(predicate);
  }
}
