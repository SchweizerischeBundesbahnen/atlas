package ch.sbb.prm.directory.controller.model.create;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.validation.DatesValidator;
import ch.sbb.prm.directory.controller.model.ToiletVersionModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldNameConstants
@Schema(name = "CreateToiletVersion")
public class CreateToiletVersionModel extends ToiletVersionModel implements DatesValidator {

  @Schema(description = "Seven digits number. First two digits represent Country Code. "
      + "Last 5 digits represent service point ID.", example = "8034505")
  @Min(AtlasFieldLengths.MIN_SEVEN_DIGITS_NUMBER)
  @Max(AtlasFieldLengths.MAX_SEVEN_DIGITS_NUMBER)
  @NotNull
  private Integer numberWithoutCheckDigit;

}
