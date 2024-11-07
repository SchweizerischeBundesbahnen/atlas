package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.lidi.enumaration.PaymentType;
import ch.sbb.atlas.validation.DatesValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

/**
 * @deprecated since V2.328.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldNameConstants
@Schema(name = "SublineVersion", description = "Deprecated in favor of SublineVersionV2")
@Deprecated(forRemoval = true, since = "2.328.0")
public class SublineVersionModel extends BaseSublineVersionModel implements DatesValidator {

  @Schema(description = "PaymentType deprecated since V2.328.0", accessMode = AccessMode.READ_ONLY)
  private PaymentType paymentType;

  @Schema(description = "Number", example = "L1")
  @Size(max = AtlasFieldLengths.LENGTH_50)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String number;

}
