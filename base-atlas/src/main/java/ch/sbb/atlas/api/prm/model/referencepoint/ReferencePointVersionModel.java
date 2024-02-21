package ch.sbb.atlas.api.prm.model.referencepoint;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointAttributeType;
import ch.sbb.atlas.api.prm.model.BasePrmStopPointChildVersionModel;
import ch.sbb.atlas.validation.DatesValidator;
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
@Schema(name = "ReferencePointVersion")
public class ReferencePointVersionModel extends BasePrmStopPointChildVersionModel implements DatesValidator {

  @Schema(description = "Main reference point")
  private boolean mainReferencePoint;

  @NotNull
  private ReferencePointAttributeType referencePointType;

}
