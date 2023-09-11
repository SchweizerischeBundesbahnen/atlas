package ch.sbb.atlas.servicepoint.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * @deprecated Didok legacy
 */
@Deprecated
public interface CodeAndDesignations {

  @NotNull
  @Schema(description = "Code, the code and the designations belongs to the Type above")
  String getCode();

  @NotNull
  @Schema(description = "Designation in german")
  String getDesignationDe();

  @NotNull
  @Schema(description = "Designation in french")
  String getDesignationFr();

  @NotNull
  @Schema(description = "Designation in italian")
  String getDesignationIt();

  @NotNull
  @Schema(description = "Designation in english")
  String getDesignationEn();

}
