package ch.sbb.atlas.api.prm.model;

import ch.sbb.atlas.api.AtlasFieldLengths;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@FieldNameConstants
public abstract class BasePrmVersionModel extends BaseBasicPrmVersionModel {

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = """
      Unique code for locations that is used in customer information.
      The structure is described in the “Swiss Location ID” specification, chapter 4.2.
      The document is available here: https://transportdatamanagement.ch/standards/""", example = "ch:1:sloid:18771:1")
  private String sloid;

}
