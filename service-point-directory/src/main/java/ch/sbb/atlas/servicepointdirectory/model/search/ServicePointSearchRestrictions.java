package ch.sbb.atlas.servicepointdirectory.model.search;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.searching.SearchRestrictions;
import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.atlas.searching.specification.IsMemberSpecification;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointRequestParams;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion.Fields;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion_;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
public class ServicePointSearchRestrictions extends SearchRestrictions<ServicePointVersion> {

  private final Pageable pageable;
  private final ServicePointRequestParams servicePointRequestParams;

  @Override
  protected SingularAttribute<ServicePointVersion, Status> getStatus() {
    return ServicePointVersion_.status;
  }

  @Override
  public Optional<LocalDate> getValidOn() {
    return Optional.ofNullable(servicePointRequestParams.getValidOn());
  }

  @Override
  public List<Status> getStatusRestrictions() {
    return servicePointRequestParams.getStatusRestrictions();
  }

  @Override
  protected SpecificationBuilder<ServicePointVersion> specificationBuilder() {
    return SpecificationBuilder.<ServicePointVersion>builder()
        .stringAttributes(
            List.of(Fields.number,
                Fields.numberShort,
                Fields.designationOfficial))
        .validFromAttribute(ServicePointVersion_.validFrom)
        .validToAttribute(ServicePointVersion_.validTo)
        .build();
  }

  @Override
  public Specification<ServicePointVersion> getSpecification() {
    return getBaseSpecification()
        .and(specificationBuilder().stringInSpecification(servicePointRequestParams.getSloids(), ServicePointVersion_.sloid))
        .and(specificationBuilder().inSpecification(servicePointRequestParams.getServicePointNumbers(), Fields.number))
        .and(specificationBuilder().inSpecification(servicePointRequestParams.getNumbersShort(), Fields.numberShort))
        .and(specificationBuilder().stringInSpecification(servicePointRequestParams.getAbbreviations(),
            ServicePointVersion_.abbreviation))
        .and(specificationBuilder().stringInSpecification(servicePointRequestParams.getBusinessOrganisationSboids(),
            ServicePointVersion_.businessOrganisation))
        .and(specificationBuilder().enumSpecification(servicePointRequestParams.getCountries(), ServicePointVersion_.country))
        .and(new IsMemberSpecification<>(servicePointRequestParams.getCategories(), ServicePointVersion_.categories))
        .and(specificationBuilder().enumSpecification(servicePointRequestParams.getOperatingPointTypes(),
            ServicePointVersion_.operatingPointType))
        .and(specificationBuilder().enumSpecification(servicePointRequestParams.getStopPointTypes(),
            ServicePointVersion_.stopPointType))
        .and(new IsMemberSpecification<>(servicePointRequestParams.getMeansOfTransport(), ServicePointVersion_.meansOfTransport))
        .and(specificationBuilder().booleanSpecification(ServicePointVersion_.operatingPoint,
            servicePointRequestParams.getOperatingPoint()))
        .and(specificationBuilder().booleanSpecification(ServicePointVersion_.operatingPointWithTimetable,
            servicePointRequestParams.getWithTimetable()))
        .and(new ValidOrEditionTimerangeSpecification(
            servicePointRequestParams.getFromDate(),
            servicePointRequestParams.getToDate(),
            servicePointRequestParams.getCreatedAfter(),
            servicePointRequestParams.getModifiedAfter()));
  }

}
