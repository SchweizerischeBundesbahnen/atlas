package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.base.service.model.entity.BaseVersion;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.base.service.versioning.model.Versionable;
import ch.sbb.atlas.servicepointdirectory.converter.CategoryConverter;
import ch.sbb.atlas.servicepointdirectory.converter.MeanOfTransportConverter;
import ch.sbb.atlas.servicepointdirectory.converter.ServicePointNumberConverter;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.enumeration.*;
import ch.sbb.atlas.servicepointdirectory.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.ServicePointStatus;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import ch.sbb.atlas.user.administration.security.BusinessOrganisationAssociated;
import javax.validation.Valid;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
@Entity(name = "service_point_version")
@AtlasVersionable
public class ServicePointVersion extends BaseVersion implements Versionable,
    BusinessOrganisationAssociated {

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
  @AtlasVersionableProperty
  private Integer numberShort;

  @NotNull
  @Enumerated(EnumType.STRING)
  private Country country;

  @Size(max = AtlasFieldLengths.LENGTH_50)
  @AtlasVersionableProperty
  private String designationLong;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_30)
  @AtlasVersionableProperty
  private String designationOfficial;

  @Size(max = AtlasFieldLengths.LENGTH_6)
  @AtlasVersionableProperty
  private String abbreviation;

  @NotNull
  @AtlasVersionableProperty
  @Enumerated(EnumType.STRING)
  private ServicePointStatus statusDidok3;

  public boolean hasGeolocation() {
    return servicePointGeolocation != null;
  }

  public boolean isFreightServicePoint() {
    return operatingPointType == OperatingPointType.FREIGHT_POINT;
  }

  @AssertTrue(message = "sortCodeOfDestinationStation only allowed for FreightServicePoints")
  boolean isValidFreightServicePoint() {
    return !isFreightServicePoint() || StringUtils.isNotBlank(sortCodeOfDestinationStation);
  }

  @Size(max = AtlasFieldLengths.LENGTH_10)
  @AtlasVersionableProperty
  private String sortCodeOfDestinationStation;

  @NotNull
  @Column(columnDefinition = "DATE")
  private LocalDate validFrom;

  @NotNull
  @Column(columnDefinition = "DATE")
  private LocalDate validTo;

  @NotBlank
  @Size(max = AtlasFieldLengths.LENGTH_50)
  @AtlasVersionableProperty
  private String businessOrganisation;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "service_point_geolocation_id", referencedColumnName = "id")
  private ServicePointGeolocation servicePointGeolocation;

  @AtlasVersionableProperty
  @ElementCollection(targetClass = Category.class, fetch = FetchType.EAGER)
  @Convert(converter = CategoryConverter.class)
  private Set<Category> categories;

  @Enumerated(EnumType.STRING)
  private OperatingPointType operatingPointType;

  @AtlasVersionableProperty
  private boolean operatingPointRouteNetwork;

  @AtlasVersionableProperty
  @Convert(converter = ServicePointNumberConverter.class)
  @Valid
  private ServicePointNumber operatingPointKilometerMaster;

  public boolean isOperatingPointKilometer() {
    return operatingPointKilometerMaster != null;
  }

  public boolean isOperatingPoint() {
    return operatingPointType != null || isTrafficPoint();
  }

  public boolean isTrafficPoint() {
    return isStopPlace() || isFreightServicePoint() || operatingPointType == OperatingPointType.TARIFF_POINT;
  }

  public boolean isBorderPoint() {
    return operatingPointType == OperatingPointType.COUNTRY_BORDER;
  }

  public Set<Category> getCategories() {
    if (categories == null) {
      return new HashSet<>();
    }
    return categories;
  }

  public boolean isStopPlace() {
    return getOperatingPointType() == OperatingPointType.STOP_POINT;
  }

  @AssertTrue
  boolean isValidStopPlace() {
    return !isStopPlace() || !getMeanOfTransport().isEmpty();
  }

  @AtlasVersionableProperty
  @ElementCollection(targetClass = MeanOfTransport.class, fetch = FetchType.EAGER)
  @Convert(converter = MeanOfTransportConverter.class)
  private Set<MeanOfTransport> meansOfTransport;

  public Set<MeanOfTransport> getMeanOfTransport() {
    if (meansOfTransport == null) {
      return new HashSet<>();
    }
    return meansOfTransport;
  }

  @Enumerated(EnumType.STRING)
  private StopPointType stopPointType;

  @AssertTrue(message = "StopPlaceType only allowed for StopPlaces")
  boolean isValidStopPlaceWithType() {
    return isStopPlace() || stopPointType == null;
  }

  @Size(max = AtlasFieldLengths.LENGTH_1500)
  @AtlasVersionableProperty
  private String comment;
}
