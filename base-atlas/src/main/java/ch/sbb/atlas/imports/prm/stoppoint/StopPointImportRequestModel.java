package ch.sbb.atlas.imports.prm.stoppoint;

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
@Schema(name = "StopPointImportRequest")
public class StopPointImportRequestModel {

  @Schema(name = "List of StopPointCsvModelContainer to import")
  @NotNull
  @NotEmpty
  private List<StopPointCsvModelContainer> stopPointCsvModelContainers;

}
