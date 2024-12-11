package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.lidi.enumaration.SublineType;
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
@Schema(name = "CreateSublineVersionV2")
public class CreateSublineVersionModelV2 extends SublineVersionModelV2 {

  @Schema(description = "Subline Type")
  @NotNull
  private SublineType sublineType;

}
