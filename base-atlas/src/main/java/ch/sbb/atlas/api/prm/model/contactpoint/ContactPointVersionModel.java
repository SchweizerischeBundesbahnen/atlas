package ch.sbb.atlas.api.prm.model.contactpoint;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.BasePrmStopPointChildVersionModel;
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
public class ContactPointVersionModel extends BasePrmStopPointChildVersionModel implements DatesValidator {

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
