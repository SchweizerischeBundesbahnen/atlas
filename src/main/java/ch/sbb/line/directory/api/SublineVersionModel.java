package ch.sbb.line.directory.api;

import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.enumaration.SublineType;
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
@Schema(name = "SublineVersion")
public class SublineVersionModel implements SequenctialValidRange {

  @Schema(description = "Technical identifier", accessMode = AccessMode.READ_ONLY)
  private Long id;

  @Schema(description = "SwissSublineNumber", example = "b1.L1.X")
  @NotBlank
  @Size(max = 50)
  private String swissSublineNumber;

  @Schema(description = "SwissLineNumber", example = "b1.L1")
  @Size(max = 50)
  private String swissLineNumber;

  @Schema(description = "Status", accessMode = AccessMode.READ_ONLY)
  private Status status;

  @Schema(description = "Subline Type")
  @NotNull
  private SublineType type;

  @Schema(description = "SLNID", accessMode = AccessMode.READ_ONLY, example = "ch:1:slnid:10001235")
  private String slnid;

  @Schema(description = "Description", example = "Meiringen - Innertkirchen")
  @Size(max = 255)
  private String description;

  @Schema(description = "Number", example = "L1")
  @Size(max = 50)
  private String number;

  @Schema(description = "LongName", example = "Spiseggfräser; Talstation - Bergstation; Ersatzbus")
  @Size(max = 1000)
  private String longName;

  @Schema(description = "PaymentType")
  @NotNull
  private PaymentType paymentType;

  @Schema(description = "Valid from")
  @NotNull
  private LocalDate validFrom;

  @Schema(description = "Valid to")
  @NotNull
  private LocalDate validTo;

  @Schema(description = "BusinessOrganisation", example = "11 - SBB - Schweizerische Bundesbahnen - 100001")
  @NotBlank
  @Size(max = 50)
  private String businessOrganisation;

}
