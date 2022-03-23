package ch.sbb.line.directory.api;

import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.enumaration.SublineType;
import ch.sbb.line.directory.validation.DatesValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "Subline")
public class SublineModel implements DatesValidator {

  @Schema(description = "SwissSublineNumber", example = "b1.L1.X")
  @NotBlank
  @Size(min = 1, max = AtlasFieldLengths.SMALL)
  private String swissSublineNumber;

  @Schema(description = "SwissLineNumber", example = "b1.L1")
  @Size(max = AtlasFieldLengths.SMALL)
  private String swissLineNumber;

  @Schema(description = "Number", example = "L1")
  @Size(max = AtlasFieldLengths.SMALL)
  @Pattern(regexp = AtlasCharacterSetsRegex.ISO_8859_1)
  private String number;

  @Schema(description = "Status", accessMode = AccessMode.READ_ONLY)
  private Status status;

  @Schema(description = "Subline Type")
  @NotNull
  private SublineType type;

  @Schema(description = "SLNID", accessMode = AccessMode.READ_ONLY, example = "ch:1:slnid:10001235")
  private String slnid;

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
