package ch.sbb.atlas.api.prm.model.relation;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.TactileVisualAttributeType;
import ch.sbb.atlas.api.prm.model.BasePrmVersionModel;
import ch.sbb.atlas.api.prm.model.PrmApiConstants;
import ch.sbb.atlas.validation.DatesValidator;
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
@Schema(name = "RelationVersion")
public class RelationVersionModel extends BasePrmVersionModel implements DatesValidator {

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = PrmApiConstants.PARENT_SLOID_DESCRIPTION, example = "ch:1:sloid:18771")
  private String parentServicePointSloid;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = """
      Reference Point Sloid: Unique code for locations that is used in customer information.
      The structure is described in the “Swiss Location ID” specification, chapter 4.2.
      The document is available here: https://transportdatamanagement.ch/standards/""", example = "ch:1:sloid:18771")
  private String referencePointSloid;

  private TactileVisualAttributeType tactileVisualMarks;

  private StandardAttributeType contrastingAreas;

  private StepFreeAccessAttributeType stepFreeAccess;



}
