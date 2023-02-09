package ch.sbb.atlas.base.service.imports.servicepoint.model;

import ch.sbb.atlas.base.service.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "ServicePointImportReRequest")
public class ServicePointImportReqModel {

  @Schema(name = "List of ServicePointsContainer to import")
  @NotNull
  @NotEmpty
  private List<ServicePointCsvModelContainer> servicePointCsvModelContainers;

}
