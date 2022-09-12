package ch.sbb.atlas.searching;

import ch.sbb.atlas.base.service.model.Status;
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

  public Specification<T> getSpecification() {
    return getBaseSpecification();
  }

  protected Specification<T> getBaseSpecification() {
    return specificationBuilder().searchCriteriaSpecification(searchCriterias)
                                 .and(specificationBuilder().validOnSpecification(validOn))
                                 .and(specificationBuilder().enumSpecification(statusRestrictions,
                                     getStatus()));
  }

  protected abstract SingularAttribute<T, Status> getStatus();

  protected abstract SpecificationBuilder<T> specificationBuilder();

}
