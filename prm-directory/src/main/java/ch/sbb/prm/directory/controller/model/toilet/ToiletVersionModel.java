package ch.sbb.prm.directory.controller.model.toilet;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.validation.DatesValidator;
import ch.sbb.prm.directory.controller.model.BasePrmVersionModel;
import ch.sbb.prm.directory.enumeration.StandardAttributeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
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
public abstract class ToiletVersionModel extends BasePrmVersionModel implements DatesValidator {

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = "Hierarchical assignment of the TPE which is to be processed to another TPE. It is a 1:1 relationship. "
      + "As key, the SLOID is used.", example = "ch:1:sloid:16161:1")
  private String parentServicePointSloid;

  @Schema(description = "Designation")
  private String designation;

  @Schema(description = "Additional Information")
  private String info;

  @Schema(description = "Wheelchair accessible toilet available")
  private StandardAttributeType wheelchairToilet;

}
