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
public class StopPointRequestParams extends BasePrmObjectRequestParams {

  @Parameter(description = "Number")
  @Singular(ignoreNullCollections = true)
  private List<
      @Min(AtlasFieldLengths.MIN_SEVEN_DIGITS_NUMBER)
      @Max(AtlasFieldLengths.MAX_SEVEN_DIGITS_NUMBER) Integer> numbers = new ArrayList<>();

  public List<ServicePointNumber> getServicePointNumbers() {
    return numbers.stream().map(ServicePointNumber::ofNumberWithoutCheckDigit).toList();
  }

}
