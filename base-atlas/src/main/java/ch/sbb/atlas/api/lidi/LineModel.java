package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.base.service.model.validation.DatesValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "Line")
public class LineModel implements DatesValidator {

  @Schema(description = "SwissLineNumber", example = "b1.L1")
  @NotBlank
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  private String swissLineNumber;

  @Schema(description = "Status", accessMode = AccessMode.READ_ONLY)
  private Status status;

  @Schema(description = "LineType")
  @NotNull
  private LineType lineType;

  @Schema(description = "SLNID", accessMode = AccessMode.READ_ONLY, example = "ch:1:slnid:10001234")
  private String slnid;

  @Schema(description = "Number", example = "L1")
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String number;

  @Schema(description = "Description", example = "Meiringen - Innertkirchen")
  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String description;

  @Schema(description = "Valid from")
  @NotNull
  private LocalDate validFrom;

  @Schema(description = "Valid to")
  @NotNull
  private LocalDate validTo;

  @Schema(description = "BusinessOrganisation SBOID", example = "ch:1:sboid:100001")
  @NotBlank
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  private String businessOrganisation;

}
