package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.servicepointdirectory.enumeration.CodeAndDesignations;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
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
@Schema(name = "CodeAndDesignation", accessMode = AccessMode.READ_ONLY)
public class CodeAndDesignation {

  @NotNull
  @Schema(description = "Code", example = "Z")
  private String code;

  @NotNull
  @Schema(description = "Designation in german", example = "Zug")
  private String designationDe;

  @NotNull
  @Schema(description = "Designation in french", example = "Train")
  private String designationFr;

  @NotNull
  @Schema(description = "Designation in italian", example = "Treno")
  private String designationIt;

  @NotNull
  @Schema(description = "Designation in english", example = "Train")
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
