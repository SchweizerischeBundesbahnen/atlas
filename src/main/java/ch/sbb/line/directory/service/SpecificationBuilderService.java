package ch.sbb.line.directory.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationBuilderService<EntityType> {

  private final List<SingularAttribute<EntityType, String>> stringAttributes;
  private final SingularAttribute<EntityType, LocalDate> validFromAttribute;
  private final SingularAttribute<EntityType, LocalDate> validToAttribute;
  private final SingularAttribute<EntityType, String> singleStringAttribute;

  public SpecificationBuilderService(List<SingularAttribute<EntityType, String>> stringAttributes,
      SingularAttribute<EntityType, LocalDate> validFromAttribute,
      SingularAttribute<EntityType, LocalDate> validToAttribute,
      SingularAttribute<EntityType, String> singleStringAttribute) {
    this.stringAttributes = stringAttributes;
    this.validFromAttribute = validFromAttribute;
    this.validToAttribute = validToAttribute;
    this.singleStringAttribute = singleStringAttribute;
  }

  public Specification<EntityType> buildSearchCriteriaSpecification(List<String> searchCriteria) {
    return new SearchCriteriaSpecification(searchCriteria, stringAttributes);
  }

  public <EnumType> Specification<EntityType> buildEnumSpecification(List<EnumType> enumRestrictions, SingularAttribute<EntityType, EnumType> enumAttribute) {
    return new EnumSpecification<EnumType>(enumRestrictions, enumAttribute);
  }

  public Specification<EntityType> buildValidOnSpecification(Optional<LocalDate> validOn) {
    return new ValidOnSpecification(validOn, validFromAttribute, validToAttribute);
  }

  public Specification<EntityType> buildSingleStringSpecification(Optional<String> searchString) {
    return new SingleStringSpecification(searchString, singleStringAttribute);
  }

  private class SearchCriteriaSpecification implements Specification<EntityType> {

    private final List<String> searchCriteria;
    private final List<SingularAttribute<EntityType, String>> searchPaths;

    public SearchCriteriaSpecification(List<String> searchCriteria, List<SingularAttribute<EntityType, String>> searchPaths) {
      this.searchCriteria = Objects.requireNonNull(searchCriteria);
      this.searchPaths = searchPaths;
    }

    @Override
    public Predicate toPredicate(Root<EntityType> root, CriteriaQuery<?> query,
        CriteriaBuilder criteriaBuilder) {
      return criteriaBuilder.and(searchCriteria.stream().map(searchString -> criteriaBuilder.or(
          searchPaths.stream().map(path -> likeIgnoreCase(criteriaBuilder, root.get(path), searchString))
              .toArray(Predicate[]::new))
      ).toArray(Predicate[]::new));
    }
  }

  private class EnumSpecification<EnumType> implements Specification<EntityType> {

    private final List<EnumType> enumRestrictions;
    private final SingularAttribute<EntityType, EnumType> enumAttribute;

    public EnumSpecification(List<EnumType> enumRestrictions, SingularAttribute<EntityType, EnumType> enumAttribute) {
      this.enumRestrictions = Objects.requireNonNull(enumRestrictions);
      this.enumAttribute = enumAttribute;
    }

    @Override
    public Predicate toPredicate(Root<EntityType> root, CriteriaQuery<?> query,
        CriteriaBuilder criteriaBuilder) {
      if (enumRestrictions.isEmpty()) {
        return criteriaBuilder.and();
      }
      return root.get(enumAttribute).in(enumRestrictions);
    }
  }

  private class ValidOnSpecification implements Specification<EntityType> {

    private final Optional<LocalDate> validOn;
    private final SingularAttribute<EntityType, LocalDate> validFromAttribute;
    private final SingularAttribute<EntityType, LocalDate> validToAttribute;

    public ValidOnSpecification(
        Optional<LocalDate> validOn,
        SingularAttribute<EntityType, LocalDate> validFromAttribute,
        SingularAttribute<EntityType, LocalDate> validToAttribute) {
      this.validOn = Objects.requireNonNull(validOn);
      this.validFromAttribute = validFromAttribute;
      this.validToAttribute = validToAttribute;
    }

    @Override
    public Predicate toPredicate(Root<EntityType> root, CriteriaQuery<?> query,
        CriteriaBuilder criteriaBuilder) {
      if (validOn.isEmpty()) {
        return criteriaBuilder.and();
      }
      return criteriaBuilder.and(
          criteriaBuilder.lessThanOrEqualTo(root.get(validFromAttribute), validOn.orElseThrow()),
          criteriaBuilder.greaterThanOrEqualTo(root.get(validToAttribute), validOn.orElseThrow())
      );
    }
  }

  private class SingleStringSpecification implements Specification<EntityType> {

    private final Optional<String> searchString;
    private final SingularAttribute<EntityType, String> stringAttribute;

    public SingleStringSpecification(Optional<String> searchString, SingularAttribute<EntityType, String> stringAttribute) {
      this.searchString = Objects.requireNonNull(searchString);
      this.stringAttribute = stringAttribute;
    }

    @Override
    public Predicate toPredicate(Root<EntityType> root, CriteriaQuery<?> query,
        CriteriaBuilder criteriaBuilder) {
      if (searchString.isEmpty()) {
        return criteriaBuilder.and();
      }
      return likeIgnoreCase(criteriaBuilder, root.get(stringAttribute),
          searchString.orElseThrow());
    }
  }

  private static Predicate likeIgnoreCase(CriteriaBuilder criteriaBuilder, Path<String> path,
      String searchString) {
    return criteriaBuilder.like(criteriaBuilder.lower(path),
        "%" + searchString.toLowerCase() + "%");
  }
}
