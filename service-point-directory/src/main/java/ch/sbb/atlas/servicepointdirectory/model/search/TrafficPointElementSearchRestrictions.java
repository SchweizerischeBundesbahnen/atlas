package ch.sbb.atlas.servicepointdirectory.model.search;

import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion.Fields;
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
public class TrafficPointElementSearchRestrictions {

  private final Pageable pageable;

  @Singular(ignoreNullCollections = true)
  private List<String> searchCriterias;

  private Optional<LocalDate> validOn;

  public Specification<TrafficPointElementVersion> getSpecification() {
    return specificationBuilder().searchCriteriaSpecification(searchCriterias)
        .and(specificationBuilder().validOnSpecification(validOn));
  }

  protected SpecificationBuilder<TrafficPointElementVersion> specificationBuilder() {
    return SpecificationBuilder.<TrafficPointElementVersion>builder()
        .stringAttributes(List.of(Fields.sloid, Fields.designation, Fields.designationOperational)).build();
  }
}
