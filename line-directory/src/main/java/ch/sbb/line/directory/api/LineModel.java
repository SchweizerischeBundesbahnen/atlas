package ch.sbb.line.directory.api;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.api.AtlasFieldLengths;
import ch.sbb.atlas.model.validation.DatesValidator;
import ch.sbb.line.directory.enumaration.LineType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
  @Size(min = 1, max = AtlasFieldLengths.SMALL)
  private String swissLineNumber;

  @Schema(description = "Status", accessMode = AccessMode.READ_ONLY)
  private Status status;

  @Schema(description = "LineType")
  @NotNull
  private LineType lineType;

  @Schema(description = "SLNID", accessMode = AccessMode.READ_ONLY, example = "ch:1:slnid:10001234")
  private String slnid;

  @Schema(description = "Number", example = "L1")
  @Size(max = AtlasFieldLengths.SMALL)
  private String number;

  @Schema(description = "Description", example = "Meiringen - Innertkirchen")
  @Size(max = AtlasFieldLengths.MID)
  private String description;

  @Schema(description = "Valid from")
  @NotNull
  private LocalDate validFrom;

  @Schema(description = "Valid to")
  @NotNull
  private LocalDate validTo;

  @Schema(description = "BusinessOrganisation", example = "11 - SBB - Schweizerische Bundesbahnen - 100001")
  @NotBlank
  @Size(min = 1, max = AtlasFieldLengths.SMALL)
  private String businessOrganisation;

}
