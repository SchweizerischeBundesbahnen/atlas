package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.CountryAndBusinessOrganisationAssociated;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.converter.MeanOfTransportConverter;
import ch.sbb.atlas.servicepoint.converter.ServicePointNumberConverter;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import ch.sbb.atlas.servicepointdirectory.converter.CategoryConverter;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.GeolocationBaseEntity;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointStatus;
import ch.sbb.atlas.validation.DatesValidator;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.atlas.versioning.model.VersionableProperty.RelationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
@Entity(name = "service_point_version")
@AtlasVersionable
public class ServicePointVersion extends BasePointVersion<ServicePointVersion> implements Versionable,
    CountryAndBusinessOrganisationAssociated, DatesValidator {

  private static final String VERSION_SEQ = "service_point_version_seq";

  @Override
  public boolean hasGeolocation() {
    return servicePointGeolocation != null;
  }

  @Override
  public void referenceGeolocationTo(ServicePointVersion version) {
    servicePointGeolocation.setServicePointVersion(version);
  }

  @Override
  public GeolocationBaseEntity geolocation() {
    return servicePointGeolocation;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotNull
  @AtlasVersionableProperty
  @Convert(converter = ServicePointNumberConverter.class)
  @Valid
  private ServicePointNumber number;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @AtlasVersionableProperty
  private String sloid;

  @NotNull
  @AtlasVersionableProperty
  private Integer numberShort;

  @NotNull
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private Country country;

  @Size(max = AtlasFieldLengths.LENGTH_50)
  @AtlasVersionableProperty
  private String designationLong;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_30)
  @AtlasVersionableProperty
  private String designationOfficial;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_6)
  @Pattern(regexp = AtlasCharacterSetsRegex.ABBREVIATION_PATTERN)
  @AtlasVersionableProperty
  private String abbreviation;

  @AtlasVersionableProperty(ignoreDiff = true)
  @Enumerated(EnumType.STRING)
  private ServicePointStatus statusDidok3;

  @Size(max = AtlasFieldLengths.LENGTH_10)
  @AtlasVersionableProperty
  private String sortCodeOfDestinationStation;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_50)
  @AtlasVersionableProperty
  private String businessOrganisation;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private OperatingPointType operatingPointType;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StopPointType stopPointType;

  @NotNull
  @Enumerated(EnumType.STRING)
  private Status status;

  @AtlasVersionableProperty
  @Convert(converter = ServicePointNumberConverter.class)
  @Valid
  private ServicePointNumber operatingPointKilometerMaster;

  @AtlasVersionableProperty
  private boolean operatingPointRouteNetwork;

  @NotNull
  @Column(columnDefinition = "DATE")
  private LocalDate validFrom;

  @NotNull
  @Column(columnDefinition = "DATE")
  private LocalDate validTo;

  @AtlasVersionableProperty
  private boolean freightServicePoint;

  @AtlasVersionableProperty
  private boolean operatingPoint;

  @AtlasVersionableProperty
  private boolean operatingPointWithTimetable;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private OperatingPointTechnicalTimetableType operatingPointTechnicalTimetableType;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private OperatingPointTrafficPointType operatingPointTrafficPointType;

  @AtlasVersionableProperty
  @ElementCollection(targetClass = Category.class, fetch = FetchType.EAGER)
  @Convert(converter = CategoryConverter.class)
  private Set<Category> categories;

  @AtlasVersionableProperty
  @ElementCollection(targetClass = MeanOfTransport.class, fetch = FetchType.EAGER)
  @Convert(converter = MeanOfTransportConverter.class)
  private Set<MeanOfTransport> meansOfTransport;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "service_point_geolocation_id", referencedColumnName = "id")
  @AtlasVersionableProperty(relationType = RelationType.ONE_TO_ONE, relationsFields = {
      ServicePointGeolocation.Fields.country,
      ServicePointGeolocation.Fields.swissCanton,
      ServicePointGeolocation.Fields.swissDistrictNumber,
      ServicePointGeolocation.Fields.swissDistrictName,
      ServicePointGeolocation.Fields.swissMunicipalityNumber,
      ServicePointGeolocation.Fields.swissMunicipalityName,
      ServicePointGeolocation.Fields.swissLocalityName,
      GeolocationBaseEntity.Fields.east,
      GeolocationBaseEntity.Fields.north,
      GeolocationBaseEntity.Fields.spatialReference,
      GeolocationBaseEntity.Fields.height
  })
  private ServicePointGeolocation servicePointGeolocation;

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
    return getCountry() == getNumber().getCountry();
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
