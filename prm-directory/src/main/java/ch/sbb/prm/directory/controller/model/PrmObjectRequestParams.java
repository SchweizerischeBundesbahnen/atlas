package ch.sbb.prm.directory.controller.model;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class PrmObjectRequestParams extends BasePrmObjectRequestParams {

  @Parameter(description = "Unique key for the associated Service Point.")
  @Singular(ignoreNullCollections = true)
  private List<String> parentServicePointSloids = new ArrayList<>();

  @Parameter(description = "Service Point Numbers")
  @Singular(ignoreNullCollections = true)
  private List<
      @Min(AtlasFieldLengths.MIN_SEVEN_DIGITS_NUMBER)
      @Max(AtlasFieldLengths.MAX_SEVEN_DIGITS_NUMBER) Integer> servicePointNumbers = new ArrayList<>();

  public List<ServicePointNumber> getNumbers() {
    return servicePointNumbers.stream().map(ServicePointNumber::ofNumberWithoutCheckDigit).toList();
  }

}
