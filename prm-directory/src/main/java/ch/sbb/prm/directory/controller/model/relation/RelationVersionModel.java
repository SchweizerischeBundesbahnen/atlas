package ch.sbb.prm.directory.controller.model.relation;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.validation.DatesValidator;
import ch.sbb.prm.directory.controller.model.BasePrmVersionModel;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.enumeration.StandardAttributeType;
import ch.sbb.prm.directory.enumeration.StepFreeAccessAttributeType;
import ch.sbb.prm.directory.enumeration.TactileVisualAttributeType;
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
public abstract class RelationVersionModel extends BasePrmVersionModel implements DatesValidator {

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = "Parent Service Point Sloid: ServiceUnique code for locations that is used in customer information. The "
      + "structure is described in the “Swiss Location ID” specification, chapter 4.2. The document is available here. "
      + "https://transportdatamanagement.ch/standards/", example = "ch:1:sloid:18771")
  private String parentServicePointSloid;

  @Schema(description = "Tactile-visual markings")
  private TactileVisualAttributeType tactileVisualMarks;

  @Schema(description = "High contrast markings")
  private StandardAttributeType contrastingAreas;

  @Schema(description = "Step-free access")
  private StepFreeAccessAttributeType stepFreeAccess;

  @Schema(description = "Reference Point Element Type")
  private ReferencePointElementType referencePointElementType;

}
