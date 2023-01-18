package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointCsvModel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "ServicePointImportResult")
public class ServicePointImportReqModel {

  @Schema(name = "List of ServicePoints to import")
  @NotNull
  @NotEmpty
  private List<ServicePointCsvModel> servicePointCsvModels;

}
