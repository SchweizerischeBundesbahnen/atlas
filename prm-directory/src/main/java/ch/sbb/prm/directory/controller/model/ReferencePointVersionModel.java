package ch.sbb.prm.directory.controller.model;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.validation.DatesValidator;
import ch.sbb.prm.directory.enumeration.ReferencePointAttributeType;
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
  @Schema(description = "Hierarchical assignment of the TPE which is to be processed to another TPE. It is a 1:1 relationship. "
      + "As key, the SLOID is used.", example = "ch:1:sloid:16161:1")
  private String parentServicePointSloid;

  @Schema(description = "Long designation of a location. Used primarily in customer information. "
      + "Not all systems can process names of this length.", example = "Biel/Bienne Bözingenfeld/Champs-de-Boujean")
  @Size(min = 2, max = AtlasFieldLengths.LENGTH_50)
  @NotNull
  private String designation;

  @Schema(description = "Main reference point")
  @NotNull
  private boolean mainReferencePoint;

  @Schema(description = "Type of reference point")
  @NotNull
  private ReferencePointAttributeType referencePointType;

}
