package ch.sbb.atlas.api.prm.model.referencepoint;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointAttributeType;
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
@Schema(name = "ReferencePointVersion")
public class ReferencePointVersionModel extends BasePrmVersionModel implements DatesValidator {

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = PrmApiConstants.PARENT_SLOID_DESCRIPTION, example = "ch:1:sloid:18771")
  @NotNull
  private String parentServicePointSloid;

  @Schema(description = "Long designation of a location. Used primarily in customer information. "
      + "Not all systems can process names of this length.", example = "Biel/Bienne Bözingenfeld/Champs-de-Boujean")
  @Size(min = 2, max = AtlasFieldLengths.LENGTH_50)
  @NotNull
  private String designation;

  @Schema(description = "Additional Information")
  private String additionalInformation;

  @Schema(description = "Main reference point")
  @NotNull
  private boolean mainReferencePoint;

  @NotNull
  private ReferencePointAttributeType referencePointType;

}
