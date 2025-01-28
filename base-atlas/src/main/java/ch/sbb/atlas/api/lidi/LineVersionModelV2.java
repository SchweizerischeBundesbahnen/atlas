package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.lidi.enumaration.LineType;
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
@Schema(name = "LineVersionV2")
public class LineVersionModelV2 extends UpdateLineVersionModelV2 {

  @Schema(description = "LineType")
  @NotNull
  private LineType lineType;
}
