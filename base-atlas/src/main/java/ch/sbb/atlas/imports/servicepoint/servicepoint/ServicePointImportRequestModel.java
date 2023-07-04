package ch.sbb.atlas.imports.servicepoint.servicepoint;

import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
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
@Schema(name = "ServicePointImportRequest")
public class ServicePointImportRequestModel {

  @Schema(name = "List of ServicePointsContainer to import")
  @NotNull
  @NotEmpty
  private List<ServicePointCsvModelContainer> servicePointCsvModelContainers;

}
