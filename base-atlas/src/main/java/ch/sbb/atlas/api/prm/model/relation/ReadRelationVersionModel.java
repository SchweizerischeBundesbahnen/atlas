package ch.sbb.atlas.api.prm.model.relation;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.validation.DatesValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
@Schema(name = "ReadRelationPointVersion")
public class ReadRelationVersionModel extends RelationVersionModel implements DatesValidator {

  @NotNull
  @Valid
  private ServicePointNumber number;

  @Schema(description = "Reference Point Element Type")
  private ReferencePointElementType referencePointElementType;

}
