package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.model.VersionedObjectDateRequestParams;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepointdirectory.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.StopPointType;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class ServicePointRequestParams extends VersionedObjectDateRequestParams {

  @Parameter(description = "Unique key for service points which is used in the customer information.")
  @Singular(ignoreNullCollections = true)
  private List<String> sloids = new ArrayList<>();

  @Parameter(description = "DiDok-Number formerly known as UIC-Code, combination of uicCountryCode and numberShort.")
  @Singular(ignoreNullCollections = true)
  private List<Integer> numbers = new ArrayList<>();

  @Parameter(description = "List of UIC Country codes.", example = "85")
  @Singular(ignoreNullCollections = true)
  private List<Integer> uicCountryCodes = new ArrayList<>();

  @Parameter(description = "List of ISO Country codes.", example = "CH")
  @Singular(ignoreNullCollections = true)
  private List<String> isoCountryCodes = new ArrayList<>();

  @Parameter(description = "Number of a service point which is provided by DiDok for Switzerland. It is part of the unique key for"
      + " service points.")
  @Singular(value = "numberShort", ignoreNullCollections = true)
  private List<Integer> numbersShort = new ArrayList<>();

  @Parameter(description = "Abbreviation of the service point.")
  @Singular(ignoreNullCollections = true)
  private List<String> abbreviations = new ArrayList<>();

  @Parameter(description = "Swiss Business Organisation ID of the business organisation.")
  @Singular(ignoreNullCollections = true)
  private List<String> businessOrganisationSboids = new ArrayList<>();

  @Parameter(description = "Country allocated the service point number and is to be interpreted organisationally, not "
      + "territorially.")
  @Singular(ignoreNullCollections = true)
  private List<Country> countries = new ArrayList<>();

  @Parameter(description = "All service points relevant for timetable planning.")
  @Singular(ignoreNullCollections = true)
  private List<OperatingPointTechnicalTimetableType> operatingPointTechnicalTimetableTypes = new ArrayList<>();

  @Parameter(description = "Assignment of service points to defined business cases.")
  @Singular(ignoreNullCollections = true)
  private List<Category> categories = new ArrayList<>();

  @Parameter(description = "Detailed intended use of a operating point.")
  @Singular(ignoreNullCollections = true)
  private List<OperatingPointType> operatingPointTypes = new ArrayList<>();

  @Parameter(description = "Indicates for which type of traffic (e.g. regular traffic) a stop was recorded.")
  @Singular(ignoreNullCollections = true)
  private List<StopPointType> stopPointTypes = new ArrayList<>();

  @Parameter(description = "Filter on the meanOfTransport.")
  @Singular(value = "meanOfTransport", ignoreNullCollections = true)
  private List<MeanOfTransport> meansOfTransport = new ArrayList<>();

  @Parameter(description = "Filter on the Status of a servicePoint.")
  @Singular(ignoreNullCollections = true)
  private List<Status> statusRestrictions = new ArrayList<>();

  @Parameter(description = "Filter on operation Points only.")
  private Boolean operatingPoint;

  @Parameter(description = "Filter on operation Points with Timetables only.")
  private Boolean withTimetable;

  public List<ServicePointNumber> getServicePointNumbers() {
    return numbers.stream().map(ServicePointNumber::ofNumberWithoutCheckDigit).toList();
  }
}
