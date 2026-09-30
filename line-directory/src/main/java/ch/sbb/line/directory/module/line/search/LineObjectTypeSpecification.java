package ch.sbb.line.directory.module.line.search;

import ch.sbb.atlas.api.lidi.enumaration.LineObjectType;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serial;
import java.util.List;
import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;

/**
 * Filters on the derived {@link LineObjectType} discriminator. A row is a {@code SUBLINE} when it carries a
 * {@code referencedLineSlnid}, otherwise it is a {@code LINE}. The discriminator is not persisted as its own column, so it cannot
 * be expressed with a plain enum specification.
 */
public class LineObjectTypeSpecification implements Specification<LineVersionV3> {

  @Serial
  private static final long serialVersionUID = 1;

  private final transient List<LineObjectType> typeRestrictions;

  public LineObjectTypeSpecification(List<LineObjectType> typeRestrictions) {
    this.typeRestrictions = typeRestrictions == null ? List.of()
        : typeRestrictions.stream().filter(Objects::nonNull).distinct().toList();
  }

  @Override
  public Predicate toPredicate(Root<LineVersionV3> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    if (typeRestrictions.isEmpty() || typeRestrictions.size() == LineObjectType.values().length) {
      return criteriaBuilder.and();
    }
    if (typeRestrictions.contains(LineObjectType.LINE)) {
      return criteriaBuilder.isNull(root.get(LineVersionV3.Fields.referencedLineSlnid));
    }
    return criteriaBuilder.isNotNull(root.get(LineVersionV3.Fields.referencedLineSlnid));
  }

}
