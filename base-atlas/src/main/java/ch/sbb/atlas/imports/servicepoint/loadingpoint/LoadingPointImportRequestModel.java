package ch.sbb.atlas.imports.servicepoint.loadingpoint;

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
@Schema(name = "LoadingPointImportRequest")
public class LoadingPointImportRequestModel {

  @Schema(name = "List of LoadingPointCsvModelContainers to import")
  @NotNull
  @NotEmpty
  private List<LoadingPointCsvModelContainer> loadingPointCsvModelContainers;

}
