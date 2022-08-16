package ch.sbb.atlas.searching;

import ch.sbb.atlas.searching.specification.SingleStringSpecification;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
public abstract class BusinessOrganisationDependentSearchRestriction<T> extends
    SearchRestrictions<T> {

  @Builder.Default
  private Optional<String> businessOrganisation = Optional.empty();

  @Override
  protected Specification<T> getBaseSpecification() {
    return super.getBaseSpecification()
                .and(new SingleStringSpecification<>(businessOrganisation, "businessOrganisation"));
  }
}
