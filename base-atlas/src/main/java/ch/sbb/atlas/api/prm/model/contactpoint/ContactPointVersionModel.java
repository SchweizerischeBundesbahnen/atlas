package ch.sbb.atlas.api.prm.model.contactpoint;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
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
@Schema(name = "ContactPointVersion")
public class ContactPointVersionModel extends BasePrmVersionModel implements DatesValidator {

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = PrmApiConstants.PARENT_SLOID_DESCRIPTION, example = "ch:1:sloid:18771")
  @NotNull
  private String parentServicePointSloid;

  @Schema(description = "Designation")
  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String designation;

  @Schema(description = "Additional Information")
  @Size(max = AtlasFieldLengths.LENGTH_2000)
  private String additionalInformation;

  @Schema(description = "Induction Loop")
  @NotNull
  private StandardAttributeType inductionLoop;

  @Schema(description = "Opening hours")
  @Size(max = AtlasFieldLengths.LENGTH_2000)
  private String openingHours;

  @Schema(description = "Wheelchair Access")
  @NotNull
  private StandardAttributeType wheelchairAccess;

  @NotNull
  private ContactPointType type;

}
