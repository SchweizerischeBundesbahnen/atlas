package ch.sbb.prm.directory.search;

import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.atlas.searching.specification.ValidOrEditionTimerangeSpecification;
import ch.sbb.prm.directory.controller.StopPointElementRequestParams;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.entity.StopPointVersion.Fields;
import ch.sbb.prm.directory.entity.StopPointVersion_;
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
public class StopPointSearchRestrictions {

  private final Pageable pageable;
  private final StopPointElementRequestParams stopPointElementRequestParams;

  @Singular(ignoreNullCollections = true)
  private List<String> searchCriterias;

  public Specification<StopPointVersion> getSpecification() {
    return specBuilder().searchCriteriaSpecification(searchCriterias)
        .and(specBuilder().validOnSpecification(Optional.ofNullable(stopPointElementRequestParams.getValidOn())))
        .and(specBuilder().inSpecification(stopPointElementRequestParams.getServicePointNumbers(), StopPointVersion.Fields.number))
        .and(specBuilder().inSpecification(stopPointElementRequestParams.getSloids(), Fields.sloid))
        .and(new ValidOrEditionTimerangeSpecification<>(
            stopPointElementRequestParams.getFromDate(),
            stopPointElementRequestParams.getToDate(),
            stopPointElementRequestParams.getCreatedAfter(),
            stopPointElementRequestParams.getModifiedAfter()));

  }

  protected SpecificationBuilder<StopPointVersion> specBuilder() {
    return SpecificationBuilder.<StopPointVersion>builder()
        .stringAttributes(List.of(StopPointVersion.Fields.number))
        .validFromAttribute(StopPointVersion_.validFrom)
        .validToAttribute(StopPointVersion_.validTo)
        .build();
  }

}
