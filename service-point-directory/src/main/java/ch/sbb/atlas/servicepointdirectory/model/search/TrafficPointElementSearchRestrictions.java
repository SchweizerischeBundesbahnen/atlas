package ch.sbb.atlas.servicepointdirectory.model.search;

import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.atlas.searching.specification.ValidOrEditionTimerangeSpecification;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion.Fields;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion_;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementRequestParams;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

@Getter
@ToString
@SuperBuilder
public class TrafficPointElementSearchRestrictions {

  private final Pageable pageable;
  private final TrafficPointElementRequestParams trafficPointElementRequestParams;

  @Singular(ignoreNullCollections = true)
  private List<String> searchCriterias;

  public Specification<TrafficPointElementVersion> getSpecification() {

    return specificationBuilder().searchCriteriaSpecification(searchCriterias)
        .and(specificationBuilder().validOnSpecification(Optional.ofNullable(trafficPointElementRequestParams.getValidOn())))
        .and(specificationBuilder().inSpecification(trafficPointElementRequestParams.getSloids(),
            TrafficPointElementVersion.Fields.sloid))
        .and(specificationBuilder().inSpecification(trafficPointElementRequestParams.getParentsloids(),
            TrafficPointElementVersion.Fields.parentSloid))
        .and(specificationBuilder().inSpecification(trafficPointElementRequestParams.getServicePointNumbers(),
            TrafficPointElementVersion.Fields.servicePointNumber))
        .and(new ServicePointNumberSboidSpecification<>(
            trafficPointElementRequestParams.getSboids(),
            trafficPointElementRequestParams.getServicePointNumbersShort().stream().flatMap(str -> Arrays.stream(str.split(","))).map(Integer::valueOf).collect(Collectors.toList()),
            trafficPointElementRequestParams.getUicCountryCodes().stream().flatMap(code -> Arrays.stream(code.split(","))).map(uicCountryCode -> Country.from(Integer.valueOf(uicCountryCode))).toList()
        ))
        .and(new ValidOrEditionTimerangeSpecification<>(
            trafficPointElementRequestParams.getFromDate(),
            trafficPointElementRequestParams.getToDate(),
            trafficPointElementRequestParams.getCreatedAfter(),
            trafficPointElementRequestParams.getModifiedAfter()));
  }

  protected SpecificationBuilder<TrafficPointElementVersion> specificationBuilder() {
    return SpecificationBuilder.<TrafficPointElementVersion>builder()
        .stringAttributes(List.of(
            TrafficPointElementVersion.Fields.sloid,
            TrafficPointElementVersion.Fields.designation,
            TrafficPointElementVersion.Fields.designationOperational))
        .validFromAttribute(TrafficPointElementVersion_.validFrom)
        .validToAttribute(TrafficPointElementVersion_.validTo)
        .build();
  }
}
