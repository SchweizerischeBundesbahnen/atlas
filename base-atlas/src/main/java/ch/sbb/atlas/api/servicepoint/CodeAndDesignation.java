package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.servicepoint.enumeration.CodeAndDesignations;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@FieldNameConstants
@Schema(name = "CodeAndDesignation", description = "Gives more detail to types")
public class CodeAndDesignation {

  @NotNull
  @Schema(description = "Code")
  private String code;

  @NotNull
  @Schema(description = "Designation in german")
  private String designationDe;

  @NotNull
  @Schema(description = "Designation in french")
  private String designationFr;

  @NotNull
  @Schema(description = "Designation in italian")
  private String designationIt;

  @NotNull
  @Schema(description = "Designation in english")
  private String designationEn;
  
  public static CodeAndDesignation fromEnum(CodeAndDesignations codeAndDesignations) {
    if (codeAndDesignations == null) {
      return null;
    }
    return CodeAndDesignation.builder()
        .code(codeAndDesignations.getCode())
        .designationDe(codeAndDesignations.getDesignationDe())
        .designationFr(codeAndDesignations.getDesignationFr())
        .designationIt(codeAndDesignations.getDesignationIt())
        .designationEn(codeAndDesignations.getDesignationEn())
        .build();
  }

}