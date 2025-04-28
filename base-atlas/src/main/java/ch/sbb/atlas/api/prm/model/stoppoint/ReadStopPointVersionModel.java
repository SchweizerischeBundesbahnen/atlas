package ch.sbb.atlas.api.prm.model.stoppoint;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
@Schema(name = "ReadStopPointVersion")
public class ReadStopPointVersionModel extends StopPointVersionModel {

  @NotNull
  @Valid
  private ServicePointNumber number;

  @Schema(description = "A stop point is reduced/complete based on the means of transport selected")
  private boolean isReduced;

}
