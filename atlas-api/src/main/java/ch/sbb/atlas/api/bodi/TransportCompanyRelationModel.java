package ch.sbb.atlas.api.bodi;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Data
@Builder
@Schema(name = "TransportCompanyRelation")
public class TransportCompanyRelationModel {

  @Schema(description = "Transport Company Id", example = "5")
  @NotNull
  private Long transportCompanyId;

  @Schema(description = "Swiss Business Organisation ID (SBOID)", example = "ch:1:sboid:100052")
  @Size(min = AtlasFieldLengths.MIN_STRING_LENGTH, max = AtlasFieldLengths.LENGTH_32)
  @NotNull
  private String sboid;

  @Schema(description = "Valid From", example = "2022-01-01")
  @NotNull
  private LocalDate validFrom;

  @Schema(description = "Valid To", example = "2022-01-01")
  @NotNull
  private LocalDate validTo;

}
