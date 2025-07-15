package ch.sbb.atlas.api.servicepoint.sector;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.model.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@FieldNameConstants
@Schema(name = "UpdateSectorGroupVersion")
public class UpdateSectorGroupVersionModel {

  @NotNull
  private LocalDate validFrom;

  @NotNull
  private LocalDate validTo;

  @Schema(description = "Designation used in the customer information systems.", example = "Bezeichnung")
  @Size(max = AtlasFieldLengths.LENGTH_8)
  private String designation;

  @Schema(description = "Length of a Sector", example = "18.000")
  @Digits(integer = 6, fraction = 3)
  @Min(0)
  private Double length;

  @Schema(description = "Optimistic locking version - instead of ETag HTTP Header (see RFC7232:Section 2.3)", example = "5")
  private Integer etagVersion;

  @Schema(description = "Status", accessMode = AccessMode.READ_ONLY)
  private Status status;

}
