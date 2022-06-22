package ch.sbb.business.organisation.directory.api;

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

  @NotNull
  private Long transportCompanyId;

  @Size(min = 1, max = 32)
  @NotNull
  private String sboid;

  @NotNull
  private LocalDate validFrom;

  @NotNull
  private LocalDate validTo;

  public static TransportCompanyRelation toEntity(TransportCompanyRelationModel model) {
    return TransportCompanyRelation.builder()
                                   .sboid(model.getSboid())
                                   .transportCompanyId(model.getTransportCompanyId())
                                   .validFrom(model.getValidFrom())
                                   .validTo(model.getValidTo()).build();
  }

  public static TransportCompanyRelationModel toModel(TransportCompanyRelation entity) {
    return TransportCompanyRelationModel.builder()
                                        .transportCompanyId(entity.getTransportCompanyId())
                                        .sboid(entity.getSboid())
                                        .validFrom(entity.getValidFrom())
                                        .validTo(entity.getValidTo()).build();
  }

}
