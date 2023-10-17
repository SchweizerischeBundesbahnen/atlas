package ch.sbb.atlas.imports.prm.stopplace;

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
@Schema(name = "StopPlaceImportRequestModel")
public class StopPlaceImportRequestModel {

  @Schema(name = "List of StopPlaceCsvModelContainer to import")
  @NotNull
  @NotEmpty
  private List<StopPlaceCsvModelContainer> stopPlaceCsvModelContainers;

}
