package ch.sbb.atlas.servicepointdirectory.model.search;

import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.atlas.searching.specification.ValidOrEditionTimerangeSpecification;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion.Fields;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion_;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementRequestParams;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter
@ToString
@SuperBuilder
public class TrafficPointElementSearchRestrictions {

  private final Pageable pageable;
  private final TrafficPointElementRequestParams trafficPointElementRequestParams;

  @Singular(ignoreNullCollections = true)
  private List<String> searchCriterias = new ArrayList<>();

  private Optional<LocalDate> validOn;

  public Specification<TrafficPointElementVersion> getSpecification() {
    List<String> sloidValues = new ArrayList<>();
    if (trafficPointElementRequestParams.getSloids() != null) {
      for (String sloidEntry : trafficPointElementRequestParams.getSloids()) {
        String[] values = sloidEntry.split(",");
        sloidValues.addAll(Arrays.asList(values));
      }
    }

    return specificationBuilder().searchCriteriaSpecification(searchCriterias)
        .and(specificationBuilder().validOnSpecification(getValidOn()))
        .and(specificationBuilder().stringInSpecification(sloidValues, TrafficPointElementVersion_.sloid))
        .and(specificationBuilder().inSpecification(trafficPointElementRequestParams.getServicePointNumbers(), Fields.servicePointNumber))
        .and(specificationBuilder().inSpecification(trafficPointElementRequestParams.getServicePointNumbersShort(), Fields.servicePointNumber))
        .and(new ValidOrEditionTimerangeSpecification<>(
            trafficPointElementRequestParams.getFromDate(),
            trafficPointElementRequestParams.getToDate(),
            trafficPointElementRequestParams.getCreatedAfter(),
            trafficPointElementRequestParams.getModifiedAfter()));
  }

  protected SpecificationBuilder<TrafficPointElementVersion> specificationBuilder() {
    return SpecificationBuilder.<TrafficPointElementVersion>builder()
        .stringAttributes(List.of(Fields.sloid, Fields.designation, Fields.designationOperational)).build();
  }
}
