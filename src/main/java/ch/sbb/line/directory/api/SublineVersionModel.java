package ch.sbb.line.directory.api;

import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.SublineType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "SublineVersion")
public class SublineVersionModel {

  @Schema(description = "Technical identifier")
  private Long id;

  @Schema(description = "Subline Type")
  private SublineType type;

  @Schema(description = "SLNID")
  private String slnid;

  @Schema(description = "Description")
  private String description;

  @Schema(description = "ShortName")
  private String shortName;

  @Schema(description = "LongName")
  private String longName;

  @Schema(description = "PaymentType")
  private PaymentType paymentType;

  @Schema(description = "Valid from")
  private LocalDate validFrom;

  @Schema(description = "Valid to")
  private LocalDate validTo;

  @Schema(description = "BusinessOrganisation")
  private String businessOrganisation;

}
