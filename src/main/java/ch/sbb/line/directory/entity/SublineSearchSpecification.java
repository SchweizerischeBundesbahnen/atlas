package ch.sbb.line.directory.entity;

import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.enumaration.SublineType;
import ch.sbb.line.directory.service.SublineSearchRestrictions;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public final class SublineSearchSpecification {

  private SublineSearchSpecification() {
    throw new IllegalStateException("Use build");
  }

  public static Specification<Subline> build(SublineSearchRestrictions sublineSearchRestrictions) {
    return new SearchCriteriaSpecification(sublineSearchRestrictions.getSearchCriteria()).and(new ValidOnSpecification(sublineSearchRestrictions.getValidOn()))
                                                          .and(new StatusSpecification(
                                                              sublineSearchRestrictions.getStatusRestrictions()))
                                                          .and(new SublineTypeSpecification(
                                                              sublineSearchRestrictions.getTypeRestrictions()));
  }

  private static class SearchCriteriaSpecification implements Specification<Subline> {

    private final List<String> searchCriteria;

    public SearchCriteriaSpecification(List<String> searchCriteria) {
      this.searchCriteria = Objects.requireNonNull(searchCriteria);
    }

    @Override
    public Predicate toPredicate(Root<Subline> root, CriteriaQuery<?> query,
        CriteriaBuilder criteriaBuilder) {

      List<Predicate> searchOnFields = new ArrayList<>();
      for (String searchString : searchCriteria) {
        searchOnFields.add(criteriaBuilder.or(
            LineSearchSpecification.likeIgnoreCase(criteriaBuilder, root.get(Subline_.swissSublineNumber), searchString),
            LineSearchSpecification.likeIgnoreCase(criteriaBuilder, root.get(Subline_.description), searchString),
            LineSearchSpecification.likeIgnoreCase(criteriaBuilder, root.get(Subline_.swissLineNumber), searchString),
            LineSearchSpecification.likeIgnoreCase(criteriaBuilder, root.get(Subline_.businessOrganisation), searchString),
            LineSearchSpecification.likeIgnoreCase(criteriaBuilder, root.get(Subline_.slnid), searchString)
        ));
      }

      return criteriaBuilder.and(searchOnFields.toArray(Predicate[]::new));
    }

  }

  private static class StatusSpecification implements Specification<Subline> {

    private final List<Status> statusRestrictions;

    public StatusSpecification(List<Status> statusRestrictions) {
      this.statusRestrictions = Objects.requireNonNull(statusRestrictions);
    }

    @Override
    public Predicate toPredicate(Root<Subline> root, CriteriaQuery<?> query,
        CriteriaBuilder criteriaBuilder) {

      if (statusRestrictions.isEmpty()) {
        return criteriaBuilder.and();
      }

      return root.get(Subline_.status).in(statusRestrictions);
    }
  }

  private static class SublineTypeSpecification implements Specification<Subline> {

    private final List<SublineType> typeRestrictions;

    public SublineTypeSpecification(List<SublineType> typeRestrictions) {
      this.typeRestrictions = Objects.requireNonNull(typeRestrictions);
    }

    @Override
    public Predicate toPredicate(Root<Subline> root, CriteriaQuery<?> query,
        CriteriaBuilder criteriaBuilder) {

      if (typeRestrictions.isEmpty()) {
        return criteriaBuilder.and();
      }

      return root.get(Subline_.type).in(typeRestrictions);
    }
  }

  private static class ValidOnSpecification implements Specification<Subline> {

    private final Optional<LocalDate> validOn;

    public ValidOnSpecification(Optional<LocalDate> validOn) {
      this.validOn = Objects.requireNonNull(validOn);
    }

    @Override
    public Predicate toPredicate(Root<Subline> root, CriteriaQuery<?> query,
        CriteriaBuilder criteriaBuilder) {

      if (validOn.isEmpty()) {
        return criteriaBuilder.and();
      }

      return criteriaBuilder.and(
          criteriaBuilder.lessThanOrEqualTo(root.get(Subline_.validFrom), validOn.orElseThrow()),
          criteriaBuilder.greaterThanOrEqualTo(root.get(Subline_.validTo), validOn.orElseThrow())
      );
    }
  }
}
