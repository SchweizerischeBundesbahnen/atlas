package ch.sbb.exportservice.job.sepodi.servicepoint;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import ch.sbb.exportservice.job.BaseEntity;
import ch.sbb.exportservice.job.sepodi.SharedBusinessOrganisation;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
public class ServicePointVersion extends BaseEntity {

  private Long id;

  private ServicePointNumber number;

  private String sloid;

  private Status status;

  private Integer numberShort;

  private Country country;

  private String designationLong;

  private String designationOfficial;

  private String abbreviation;

  private String sortCodeOfDestinationStation;

  private SharedBusinessOrganisation sharedBusinessOrganisation;

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