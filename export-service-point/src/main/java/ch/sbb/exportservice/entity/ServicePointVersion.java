package ch.sbb.exportservice.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
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
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.exportservice.entity.converter.CategoryConverter;
import ch.sbb.exportservice.entity.converter.MeanOfTransportConverter;
import ch.sbb.exportservice.entity.converter.ServicePointNumberConverter;
import ch.sbb.exportservice.entity.geolocation.ServicePointGeolocation;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

  private static final String VERSION_SEQ = "service_point_version_seq";

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
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private Status status;

  @NotNull
  @AtlasVersionableProperty
  @Column(name = "number_short")
  private Integer numberShort;

  @NotNull
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private Country country;

  @Size(max = AtlasFieldLengths.LENGTH_50)
  @AtlasVersionableProperty
  @Column(name = "designation_long")
  private String designationLong;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_30)
  @AtlasVersionableProperty
  @Column(name = "designation_official")
  private String designationOfficial;

  @Size(max = AtlasFieldLengths.LENGTH_6)
  @AtlasVersionableProperty
  private String abbreviation;

  @NotNull
  @AtlasVersionableProperty
  @Enumerated(EnumType.STRING)
  @Column(name = "status_didok3")
  private ServicePointStatus statusDidok3;

  @Size(max = AtlasFieldLengths.LENGTH_10)
  @AtlasVersionableProperty
  @Column(name = "sort_code_of_destination_station")
  private String sortCodeOfDestinationStation;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_50)
  @AtlasVersionableProperty
  @Column(name = "business_organisation")
  private String businessOrganisation;

  @AtlasVersionableProperty
  @ElementCollection(targetClass = Category.class, fetch = FetchType.EAGER)
  @Convert(converter = CategoryConverter.class)
  private Set<Category> categories;
  private String categoriesPipeList;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty @Column(name = "operating_point_type")
  private OperatingPointType operatingPointType;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  @Column(name = "operating_point_technical_timetable_type")
  private OperatingPointTechnicalTimetableType operatingPointTechnicalTimetableType;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  @Column(name = "operating_point_traffic_point_type")
  private OperatingPointTrafficPointType operatingPointTrafficPointType;

  @AtlasVersionableProperty
  @Column(name = "operating_point_route_network")
  private boolean operatingPointRouteNetwork;

  @AtlasVersionableProperty
  @Column(name = "freight_service_point")
  private boolean freightServicePoint;

  @AtlasVersionableProperty
  @Column(name = "operating_point")
  private boolean operatingPoint;

  @AtlasVersionableProperty

  @Column(name = "operating_point_with_timetable")
  private boolean operatingPointWithTimetable;

  @AtlasVersionableProperty
  @Convert(converter = ServicePointNumberConverter.class)
  @Valid
  @Column(name = "operating_point_kilometer_master")
  private ServicePointNumber operatingPointKilometerMaster;

  @AtlasVersionableProperty
  @ElementCollection(targetClass = MeanOfTransport.class, fetch = FetchType.EAGER)
  @CollectionTable(name = "service_point_version_means_of_transport")
  @Convert(converter = MeanOfTransportConverter.class)
  @Column(name = "means_of_transport")
  private Set<MeanOfTransport> meansOfTransport;

  private String meansOfTransportPipeList;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  @Column(name = "stop_point_type")
  private StopPointType stopPointType;

  @Size(max = AtlasFieldLengths.LENGTH_1500)
  @AtlasVersionableProperty
  @Column(name = "comment")
  private String comment;

  private ServicePointGeolocation servicePointGeolocation;

  @NotNull
  @Column(name = "valid_from", columnDefinition = "DATE")
  private LocalDate validFrom;

  @NotNull
  @Column(name = "valid_to", columnDefinition = "DATE")
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