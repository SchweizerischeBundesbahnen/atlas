package ch.sbb.atlas.servicepointdirectory.model.search;

import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion.Fields;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@Builder
public class LoadingPointSearchRestrictions {

  private final Pageable pageable;

  @Singular(ignoreNullCollections = true)
  private List<String> searchCriterias;

  private Optional<LocalDate> validOn;

  public Specification<LoadingPointVersion> getSpecification() {
    return specificationBuilder().searchCriteriaSpecification(searchCriterias)
        .and(specificationBuilder().validOnSpecification(validOn));
  }

  protected SpecificationBuilder<LoadingPointVersion> specificationBuilder() {
    return SpecificationBuilder.<LoadingPointVersion>builder()
        .stringAttributes(List.of(Fields.number, Fields.designation, Fields.designationLong)).build();
  }
}
