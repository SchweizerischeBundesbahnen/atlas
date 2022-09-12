package ch.sbb.business.organisation.directory.api;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
import ch.sbb.business.organisation.directory.entity.TransportCompanyRelation;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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

  @Schema(description = "Transport Company Id", required = true, example = "5")
  @NotNull
  private Long transportCompanyId;

  @Schema(description = "Swiss Business Organisation ID (SBOID)", example = "ch:1:sboid:100052", required = true)
  @Size(min = AtlasFieldLengths.MIN_STRING_LENGTH, max = AtlasFieldLengths.LENGTH_32)
  @NotNull
  private String sboid;

  @Schema(description = "Valid From", example = "2022-01-01", required = true)
  @NotNull
  private LocalDate validFrom;

  @Schema(description = "Valid To", example = "2022-01-01", required = true)
  @NotNull
  private LocalDate validTo;

  public static TransportCompanyRelation toEntity(TransportCompanyRelationModel model, TransportCompany transportCompany) {
    return TransportCompanyRelation.builder()
                                   .sboid(model.getSboid())
                                   .transportCompany(transportCompany)
                                   .validFrom(model.getValidFrom())
                                   .validTo(model.getValidTo()).build();
  }

  public static TransportCompanyRelationModel toModel(TransportCompanyRelation entity) {
    return TransportCompanyRelationModel.builder()
                                        .transportCompanyId(entity.getTransportCompany().getId())
                                        .sboid(entity.getSboid())
                                        .validFrom(entity.getValidFrom())
                                        .validTo(entity.getValidTo()).build();
  }

}
