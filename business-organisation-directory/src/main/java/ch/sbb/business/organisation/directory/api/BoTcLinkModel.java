package ch.sbb.business.organisation.directory.api;

import ch.sbb.business.organisation.directory.entity.BoTcLink;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
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
@Schema(name = "BusinessOrganisationTransportCompanyLink")
public class BoTcLinkModel {

  @NotNull
  private Integer transportCompanyId;

  @Size(min = 1, max = 32)
  @NotNull
  private String sboid;

  @NotNull
  private LocalDate validFrom;

  @NotNull
  private LocalDate validTo;

  public static BoTcLink toEntity(BoTcLinkModel model) {
    return BoTcLink.builder()
                   .sboid(model.getSboid())
                   .transportCompanyId(model.getTransportCompanyId())
                   .validFrom(model.getValidFrom())
                   .validTo(model.getValidTo()).build();
  }

  public static BoTcLinkModel toModel(BoTcLink entity) {
    return BoTcLinkModel.builder()
                        .transportCompanyId(entity.getTransportCompanyId())
                        .sboid(entity.getSboid())
                        .validFrom(entity.getValidFrom())
                        .validTo(entity.getValidTo()).build();
  }

}
