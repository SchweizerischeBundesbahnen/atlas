package ch.sbb.prm.directory.controller;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.VersionedObjectDateRequestParams;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class StopPointRequestParams extends VersionedObjectDateRequestParams {

  @Parameter(description = "Unique key for stop points which is used in the customer information.")
  @Singular(ignoreNullCollections = true)
  private List<String> sloids = new ArrayList<>();

  @Parameter(description = "Number")
  @Singular(ignoreNullCollections = true)
  private List<
      @Min(AtlasFieldLengths.MIN_SEVEN_DIGITS_NUMBER)
      @Max(AtlasFieldLengths.MAX_SEVEN_DIGITS_NUMBER) Integer> numbers = new ArrayList<>();

  public List<ServicePointNumber> getServicePointNumbers() {
    return numbers.stream().map(ServicePointNumber::ofNumberWithoutCheckDigit).toList();
  }

}
