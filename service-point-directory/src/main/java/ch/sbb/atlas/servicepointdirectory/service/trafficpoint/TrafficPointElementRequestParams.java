package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.VersionedObjectDateRequestParams;
import ch.sbb.atlas.location.SloidHelper;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.ArrayList;
import java.util.Arrays;
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
public class TrafficPointElementRequestParams extends VersionedObjectDateRequestParams {

  @Parameter(description = "Unique key for service points which is used in the customer information.")
  @Singular(ignoreNullCollections = true)
  private List<String> sloids = new ArrayList<>();

  @Parameter(description = "DiDok-Number formerly known as UIC-Code, combination of uicCountryCode and numberShort.")
  @Singular(ignoreNullCollections = true)
  private List<
      @Min(AtlasFieldLengths.MIN_SEVEN_DIGITS_NUMBER)
      @Max(AtlasFieldLengths.MAX_SEVEN_DIGITS_NUMBER) String> servicePointNumbers = new ArrayList<>();

  @Parameter(description = "Sloid of the parent traffic point, usually an area")
  @Singular(ignoreNullCollections = true)
  private List<String> parentsloids = new ArrayList<>();

  @Parameter(description = "sboid")
  @Singular(ignoreNullCollections = true)
  private List<String> sboids = new ArrayList<>();

  @Parameter(description = "Unique key for the associated Service Point.")
  @Singular(ignoreNullCollections = true)
  private List<String> parentServicePointSloids = new ArrayList<>();

  @Parameter(description = "List of UIC Country codes. The UIC Country code applies to the country of the service point number")
  @Singular(ignoreNullCollections = true)
  private List<String> uicCountryCodes = new ArrayList<>();

  @Parameter(description =
      "Number of a service point which is provided by DiDok for Switzerland. It is part of the unique key for"
          + " service points.")
  @Singular(value = "numberShort", ignoreNullCollections = true)
  private List<String> servicePointNumbersShort = new ArrayList<>();

  @Parameter(description = "Type of the TrafficPoint")
  private TrafficPointElementType trafficPointElementType;

  @JsonIgnore
  public List<ServicePointNumber> getServicePointNumbersWithoutCheckDigit() {
    return servicePointNumbers.stream()
        .flatMap(str -> Arrays.stream(str.split(","))
            .map(String::trim))
        .map(Integer::valueOf)
        .map(ServicePointNumber::ofNumberWithoutCheckDigit)
        .toList();
  }

  @JsonIgnore
  public List<ServicePointNumber> getServicePointNumbersToFilter() {
    List<ServicePointNumber> numbers = new ArrayList<>(getServicePointNumbersWithoutCheckDigit());
    List<ServicePointNumber> sloidNumbers = getParentServicePointSloids()
        .stream()
        .map(SloidHelper::getServicePointNumber)
        .toList();
    numbers.addAll(sloidNumbers);
    return numbers.stream().distinct().toList();
  }

}
