package ch.sbb.atlas.api.prm.model.toilet;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.BasePrmVersionModel;
import ch.sbb.atlas.api.prm.model.PrmApiConstants;
import ch.sbb.atlas.validation.DatesValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
@Schema(name = "ToiletVersion")
public class ToiletVersionModel extends BasePrmVersionModel implements DatesValidator {

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = PrmApiConstants.PARENT_SLOID_DESCRIPTION, example = "ch:1:sloid:18771")
  @NotNull
  private String parentServicePointSloid;

  @Schema(description = "Designation")
  private String designation;

  @Schema(description = "Additional Information")
  private String additionalInformation;

  private StandardAttributeType wheelchairToilet;

}
