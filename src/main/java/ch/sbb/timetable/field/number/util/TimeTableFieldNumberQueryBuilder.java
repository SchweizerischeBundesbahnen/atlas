package ch.sbb.timetable.field.number.util;

import ch.sbb.timetable.field.number.entity.TimetableFieldNumber;
import ch.sbb.timetable.field.number.enumaration.Status;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Component;

@Component
public class TimeTableFieldNumberQueryBuilder {

  private final CriteriaBuilder criteriaBuilder;
  private final CriteriaQuery<TimetableFieldNumber> criteriaQuery;
  private final Root<TimetableFieldNumber> timetableFieldNumberRoot;

  public TimeTableFieldNumberQueryBuilder(EntityManager entityManager) {
    criteriaBuilder = entityManager.getCriteriaBuilder();
    criteriaQuery = criteriaBuilder.createQuery(TimetableFieldNumber.class);
    timetableFieldNumberRoot = criteriaQuery.from(TimetableFieldNumber.class);
  }

  private static Set<String> getStringFieldNames() {
    Field[] fields = TimetableFieldNumber.class.getDeclaredFields();
    return Arrays.stream(fields)
        .filter(field -> field.getType() == String.class)
        .map(Field::getName)
        .collect(Collectors.toSet());
  }

  public Predicate getValidityPredicate(LocalDate validOn) {
    // TODO: AnnotationProcessor
    Path<LocalDate> validFrom = timetableFieldNumberRoot.get("validFrom");
    Path<LocalDate> validTo = timetableFieldNumberRoot.get("validTo");
    return validOn != null ? criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(validFrom, validOn),
        criteriaBuilder.greaterThanOrEqualTo(validTo, validOn)) : criteriaBuilder.and();
  }

  public Predicate getStatusPredicate(Set<String> statuses) {
    // TODO: AnnotationProcessor
    if (statuses.isEmpty()) {
      return criteriaBuilder.and();
    }
    Path<Status> status = timetableFieldNumberRoot.get("status");
    return criteriaBuilder.or(statuses.stream()
        .map(searchString -> Arrays.stream(Status.values())
            .filter(statusVal -> statusVal.isStatus(searchString.toUpperCase()))
            .findFirst())
        .filter(Optional::isPresent)
        .map(statusOptional -> criteriaBuilder.equal(status, statusOptional.get()))
        .toArray(Predicate[]::new));
  }

  public Predicate getStringPredicate(Set<String> searchStrings) {
    if (searchStrings.isEmpty()) {
      return criteriaBuilder.and();
    }
    List<Path<String>> stringPaths = getStringFieldNames().stream().map(timetableFieldNumberRoot::<String>get).collect(Collectors.toList());
    Predicate[] stringPredicates = searchStrings.stream().map(searchString -> criteriaBuilder.or(
        stringPaths.stream().map(objectPath -> criteriaBuilder.like(
                criteriaBuilder.lower(objectPath), "%" + searchString.toLowerCase() + "%"))
            .toArray(Predicate[]::new)
    )).toArray(Predicate[]::new);
    return criteriaBuilder.and(stringPredicates);
  }

  public CriteriaQuery<TimetableFieldNumber> queryAll(List<String> searchStrings, LocalDate validOn) {
    Set<String> statuses = new HashSet<>();
    Set<String> strings = new HashSet<>();
    if (searchStrings != null) {
      statuses = searchStrings.stream()
          .filter(searchString -> Arrays.stream(Status.values())
              .anyMatch(statusVal -> statusVal.isStatus(searchString.toUpperCase()))).collect(Collectors.toSet());
      Set<String> finalStatuses = statuses;
      strings = searchStrings.stream().filter(s -> !finalStatuses.contains(s)).collect(Collectors.toSet());
    }
    Predicate all = criteriaBuilder.and(
        getStringPredicate(strings),
        getValidityPredicate(validOn),
        getStatusPredicate(statuses));
    return criteriaQuery.select(timetableFieldNumberRoot).where(all);
  }

}
