package ch.sbb.line.directory.entity;

import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.service.LineSearchRestrictions;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public final class LineSearchSpecification {

  private LineSearchSpecification() {
    throw new IllegalStateException("Use build");
  }

  public static Specification<Line> build(LineSearchRestrictions lineSearchRestrictions) {
    return new SearchCriteriaSpecification(lineSearchRestrictions.getSearchCriteria()).and(
                                                                                          new ValidOnSpecification(lineSearchRestrictions.getValidOn()))
                                                                                      .and(
                                                                                          new StatusSpecification(
                                                                                              lineSearchRestrictions.getStatusRestrictions()))
                                                                                      .and(
                                                                                          new LineTypeSpecification(
                                                                                              lineSearchRestrictions.getTypeRestrictions()))
                                                                                      .and(
                                                                                          new SwissLineNumberSpecification(
                                                                                              lineSearchRestrictions.getSwissLineNumber()));
  }

  private static class SearchCriteriaSpecification implements Specification<Line> {

    private final List<String> searchCriteria;

    public SearchCriteriaSpecification(List<String> searchCriteria) {
      this.searchCriteria = Objects.requireNonNull(searchCriteria);
    }

    @Override
    public Predicate toPredicate(Root<Line> root, CriteriaQuery<?> query,
        CriteriaBuilder criteriaBuilder) {

      List<Predicate> searchOnFields = new ArrayList<>();
      for (String searchString : searchCriteria) {
        searchOnFields.add(criteriaBuilder.or(
            likeIgnoreCase(criteriaBuilder, root.get(Line_.swissLineNumber), searchString),
            likeIgnoreCase(criteriaBuilder, root.get(Line_.number), searchString),
            likeIgnoreCase(criteriaBuilder, root.get(Line_.description), searchString),
            likeIgnoreCase(criteriaBuilder, root.get(Line_.businessOrganisation), searchString),
            likeIgnoreCase(criteriaBuilder, root.get(Line_.slnid), searchString)
        ));
      }

      return criteriaBuilder.and(searchOnFields.toArray(Predicate[]::new));
    }

  }

  private static class StatusSpecification implements Specification<Line> {

    private final List<Status> statusRestrictions;

    public StatusSpecification(List<Status> statusRestrictions) {
      this.statusRestrictions = Objects.requireNonNull(statusRestrictions);
    }

    @Override
    public Predicate toPredicate(Root<Line> root, CriteriaQuery<?> query,
        CriteriaBuilder criteriaBuilder) {

      if (statusRestrictions.isEmpty()) {
        return criteriaBuilder.and();
      }

      return root.get(Line_.status).in(statusRestrictions);
    }
  }

  private static class LineTypeSpecification implements Specification<Line> {

    private final List<LineType> typeRestrictions;

    public LineTypeSpecification(List<LineType> typeRestrictions) {
      this.typeRestrictions = Objects.requireNonNull(typeRestrictions);
    }

    @Override
    public Predicate toPredicate(Root<Line> root, CriteriaQuery<?> query,
        CriteriaBuilder criteriaBuilder) {

      if (typeRestrictions.isEmpty()) {
        return criteriaBuilder.and();
      }

      return root.get(Line_.type).in(typeRestrictions);
    }
  }

  private static class ValidOnSpecification implements Specification<Line> {

    private final Optional<LocalDate> validOn;

    public ValidOnSpecification(Optional<LocalDate> validOn) {
      this.validOn = Objects.requireNonNull(validOn);
    }

    @Override
    public Predicate toPredicate(Root<Line> root, CriteriaQuery<?> query,
        CriteriaBuilder criteriaBuilder) {

      if (validOn.isEmpty()) {
        return criteriaBuilder.and();
      }

      return criteriaBuilder.and(
          criteriaBuilder.lessThanOrEqualTo(root.get(Line_.validFrom), validOn.orElseThrow()),
          criteriaBuilder.greaterThanOrEqualTo(root.get(Line_.validTo), validOn.orElseThrow())
      );
    }
  }

  private static class SwissLineNumberSpecification implements Specification<Line> {

    private final Optional<String> swissLineNumber;

    public SwissLineNumberSpecification(Optional<String> swissLineNumber) {
      this.swissLineNumber = Objects.requireNonNull(swissLineNumber);
    }

    @Override
    public Predicate toPredicate(Root<Line> root, CriteriaQuery<?> query,
        CriteriaBuilder criteriaBuilder) {

      if (swissLineNumber.isEmpty()) {
        return criteriaBuilder.and();
      }

      return likeIgnoreCase(criteriaBuilder, root.get(Line_.swissLineNumber),
          swissLineNumber.orElseThrow());
    }
  }

  public static Predicate likeIgnoreCase(CriteriaBuilder criteriaBuilder, Path<String> path,
      String searchString) {
    return criteriaBuilder.like(criteriaBuilder.lower(path),
        "%" + searchString.toLowerCase() + "%");
  }
}
