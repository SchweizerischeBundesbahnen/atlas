package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.BaseVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.validation.DatesValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldNameConstants
@Schema(name = "LoadingPointVersion")
public class LoadingPointVersionModel extends BaseVersionModel implements DatesValidator {

  @Schema(description = "Technical identifier", accessMode = AccessMode.READ_ONLY, example = "1")
  private Long id;

  @NotNull
  @Schema(description = "Loading Point Number", example = "4201")
  private Integer number;

  @NotNull
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_12)
  @Schema(description = "Designation", example = "Piazzale")
  private String designation;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_35)
  @Schema(description = "Designation Long", example = "Piazzale")
  private String designationLong;

  @Schema(description = "Is a connectionPoint")
  private boolean connectionPoint;

  @NotNull
  @Valid
  private ServicePointNumber servicePointNumber;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = "Unique code for locations that is used in customer information. The structure is described in the "
          + "“Swiss Location ID” specification, chapter 4.2. The document is available here. "
          + "https://transportdatamanagement.ch/standards/", example = "ch:1:sloid:18771")
  private String servicePointSloid;

  @NotNull
  private LocalDate validFrom;

  @NotNull
  private LocalDate validTo;

  public String getServicePointSloid(){
    return ServicePointNumber.calculateSloid(this.servicePointNumber);
  }

}
