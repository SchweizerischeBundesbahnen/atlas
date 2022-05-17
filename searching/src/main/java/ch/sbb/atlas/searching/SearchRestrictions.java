package ch.sbb.atlas.searching;

import ch.sbb.atlas.model.Status;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.persistence.metamodel.SingularAttribute;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
public abstract class SearchRestrictions<T> {

  private final Pageable pageable;

  @Singular(ignoreNullCollections = true)
  private List<String> searchCriterias;

  @Singular(ignoreNullCollections = true)
  private List<Status> statusRestrictions;

  @Builder.Default
  private Optional<LocalDate> validOn = Optional.empty();

  protected Specification<T> getBaseSpecification(SpecificationBuilder<T> specificationBuilder) {
    return specificationBuilder.searchCriteriaSpecification(searchCriterias)
                               .and(specificationBuilder.validOnSpecification(validOn))
                               .and(specificationBuilder.enumSpecification(statusRestrictions,
                                   getStatus()));
  }

  protected abstract SingularAttribute<T, Status> getStatus();

  public Specification<T> getSpecification(SpecificationBuilder<T> specificationBuilder) {
    return getBaseSpecification(specificationBuilder);
  }

}
