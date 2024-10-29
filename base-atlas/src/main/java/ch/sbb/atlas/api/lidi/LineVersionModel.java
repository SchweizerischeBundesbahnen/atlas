package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.lidi.enumaration.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "LineVersion", description = "Deprecated in favor of LineVersionV2")
@Deprecated(forRemoval = true, since = "2.328.0")
public class LineVersionModel extends BaseLineVersionModel {

  @Schema(description = "PaymentType")
  @NotNull
  private PaymentType paymentType;

}
