package ch.sbb.atlas.imports.servicepoint.trafficpoint;

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
@Schema(name = "TrafficPointImportRequest")
public class TrafficPointImportRequestModel {

  @Schema(name = "List of TrafficPointContainers to import")
  @NotNull
  @NotEmpty
  private List<TrafficPointCsvModelContainer> trafficPointCsvModelContainers;

}
