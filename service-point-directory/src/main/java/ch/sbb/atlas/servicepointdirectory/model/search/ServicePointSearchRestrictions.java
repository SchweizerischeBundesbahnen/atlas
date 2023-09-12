package ch.sbb.atlas.servicepointdirectory.model.search;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.searching.SearchRestrictions;
import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.atlas.searching.specification.ElementCollectionContainsAnySpecification;
import ch.sbb.atlas.searching.specification.EnumByConversionServicePointGeolocationSpecification;
import ch.sbb.atlas.searching.specification.EnumByConversionSpecification;
import ch.sbb.atlas.searching.specification.ValidOrEditionTimerangeSpecification;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion_;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation_;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointRequestParams;
import jakarta.persistence.metamodel.SingularAttribute;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
            List.of(ServicePointVersion.Fields.number,
                ServicePointVersion.Fields.numberShort,
                ServicePointVersion.Fields.designationOfficial))
        .validFromAttribute(ServicePointVersion_.validFrom)
        .validToAttribute(ServicePointVersion_.validTo)
        .build();
  }

  @Override
  public Specification<ServicePointVersion> getSpecification() {
    return getBaseSpecification()
        .and(specificationBuilder().stringInSpecification(servicePointRequestParams.getSloids(), ServicePointVersion_.sloid))
        .and(specificationBuilder().inSpecification(servicePointRequestParams.getServicePointNumbers(),
            ServicePointVersion.Fields.number))
        .and(specificationBuilder().inSpecification(servicePointRequestParams.getNumbersShort(),
            ServicePointVersion.Fields.numberShort))
        .and(specificationBuilder().stringInSpecification(servicePointRequestParams.getAbbreviations(),
            ServicePointVersion_.abbreviation))
        .and(specificationBuilder().stringInSpecification(servicePointRequestParams.getBusinessOrganisationSboids(),
            ServicePointVersion_.businessOrganisation))
        .and(specificationBuilder().enumSpecification(servicePointRequestParams.getCountries(), ServicePointVersion_.country))
        .and(new ElementCollectionContainsAnySpecification<>(servicePointRequestParams.getCategories(),
            ServicePointVersion_.categories))
        .and(specificationBuilder().enumSpecification(servicePointRequestParams.getOperatingPointTypes(),
            ServicePointVersion_.operatingPointType))
        .and(specificationBuilder().enumSpecification(servicePointRequestParams.getStopPointTypes(),
            ServicePointVersion_.stopPointType))
        .and(new ElementCollectionContainsAnySpecification<>(servicePointRequestParams.getMeansOfTransport(),
            ServicePointVersion_.meansOfTransport))
        .and(specificationBuilder().enumSpecification(servicePointRequestParams.getOperatingPointTechnicalTimetableTypes(),
            ServicePointVersion_.operatingPointTechnicalTimetableType))
        .and(specificationBuilder().booleanSpecification(ServicePointVersion_.operatingPoint,
            servicePointRequestParams.getOperatingPoint()))
        .and(specificationBuilder().booleanSpecification(ServicePointVersion_.operatingPointWithTimetable,
            servicePointRequestParams.getWithTimetable()))
        .and(new EnumByConversionSpecification<>(servicePointRequestParams.getUicCountryCodes(), Country::from,
            ServicePointVersion_.country))
        .and(new EnumByConversionServicePointGeolocationSpecification<>(servicePointRequestParams.getIsoCountryCodes(),
            Country::fromIsoCode, ServicePointVersion_.servicePointGeolocation,
            ServicePointGeolocation_.country))
        .and(new ValidOrEditionTimerangeSpecification<>(
            servicePointRequestParams.getFromDate(),
            servicePointRequestParams.getToDate(),
            servicePointRequestParams.getCreatedAfter(),
            servicePointRequestParams.getModifiedAfter()));
  }

}
