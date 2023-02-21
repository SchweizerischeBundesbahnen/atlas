package ch.sbb.atlas.servicepointdirectory.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public interface CodeAndDesignations {

  @NotNull
  @Schema(description = "Code", example = "Z")
  String getCode();

  @NotNull
  @Schema(description = "Designation in german", example = "Zug")
  String getDesignationDe();

  @NotNull
  @Schema(description = "Designation in french", example = "Train")
  String getDesignationFr();

  @NotNull
  @Schema(description = "Designation in italian", example = "Treno")
  String getDesignationIt();

  @NotNull
  @Schema(description = "Designation in english", example = "Train")
  String getDesignationEn();

}
