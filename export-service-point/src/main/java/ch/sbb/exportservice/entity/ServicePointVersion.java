package ch.sbb.exportservice.entity;

import ch.sbb.atlas.api.model.BusinessOrganisationAssociated;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepoint.enumeration.ServicePointStatus;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import ch.sbb.atlas.validation.DatesValidator;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.exportservice.entity.geolocation.ServicePointGeolocation;
import jakarta.validation.constraints.AssertTrue;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
public class ServicePointVersion extends BaseDidokImportEntity implements Versionable,
    BusinessOrganisationAssociated, DatesValidator {

  private Long id;

  private ServicePointNumber number;

  private String sloid;

  private Status status;

  private Integer numberShort;

  private Country country;

  private String designationLong;

  private String designationOfficial;

  private String abbreviation;

  private ServicePointStatus statusDidok3;
  private String sortCodeOfDestinationStation;

  private String businessOrganisation;

  private Set<Category> categories;
  private String categoriesPipeList;
  private OperatingPointType operatingPointType;

  private OperatingPointTechnicalTimetableType operatingPointTechnicalTimetableType;

  private OperatingPointTrafficPointType operatingPointTrafficPointType;

  private boolean operatingPointRouteNetwork;

  private boolean freightServicePoint;

  private boolean operatingPoint;

  private boolean operatingPointWithTimetable;

  private ServicePointNumber operatingPointKilometerMaster;

  private Set<MeanOfTransport> meansOfTransport;

  private String meansOfTransportPipeList;

  private StopPointType stopPointType;

  private String comment;

  private ServicePointGeolocation servicePointGeolocation;
  private LocalDate validFrom;
  private LocalDate validTo;

  public boolean hasGeolocation() {
    return servicePointGeolocation != null;
  }

  @ToString.Include
  public boolean isStopPoint() {
    return !getMeansOfTransport().isEmpty();
  }

  @ToString.Include
  public boolean isFareStop() {
    return operatingPointTrafficPointType == OperatingPointTrafficPointType.TARIFF_POINT;
  }

  @ToString.Include
  public boolean isTrafficPoint() {
    return isStopPoint() || isFreightServicePoint() || isFareStop();
  }

  @ToString.Include
  public boolean isBorderPoint() {
    return operatingPointTechnicalTimetableType == OperatingPointTechnicalTimetableType.COUNTRY_BORDER;
  }

  @ToString.Include
  public boolean isOperatingPointKilometer() {
    return operatingPointKilometerMaster != null;
  }

  @AssertTrue(message = "StopPointType only allowed for StopPoint")
  boolean isValidStopPointWithType() {
    return isStopPoint() || stopPointType == null;
  }

  @AssertTrue(message = "FreightServicePoint in CH needs sortCodeOfDestinationStation")
  public boolean isValidFreightServicePoint() {
    return !(country == Country.SWITZERLAND && freightServicePoint && !getValidFrom().isBefore(LocalDate.now()))
        || StringUtils.isNotBlank(sortCodeOfDestinationStation);
  }

  @AssertTrue(message = "Country needs to be the same as in ServicePointNumber")
  public boolean isValidCountry() {
    return Objects.equals(getCountry(), getNumber().getCountry());
  }

  @AssertTrue(message = "At most one of OperatingPointTechnicalTimetableType, "
      + "OperatingPointTrafficPointType may be set")
  public boolean isValidType() {
    long mutualTypes = Stream.of(
            getOperatingPointTechnicalTimetableType() != null,
            getOperatingPointTrafficPointType() != null)
        .filter(i -> i)
        .count();
    return mutualTypes <= 1;
  }

  public Set<MeanOfTransport> getMeansOfTransport() {
    if (meansOfTransport == null) {
      return new HashSet<>();
    }
    return meansOfTransport;
  }

  public Set<Category> getCategories() {
    if (categories == null) {
      return new HashSet<>();
    }
    return categories;
  }
}