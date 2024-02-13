package ch.sbb.atlas.imports.prm.platform;

import ch.sbb.atlas.imports.prm.toilet.ToiletCsvModelContainer;
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
@Schema(name = "ToiletImportRequest")
public class ToiletImportRequestModel {

  @Schema(name = "List of ToiletCsvModelContainer to import")
  @NotNull
  @NotEmpty
  private List<ToiletCsvModelContainer> toiletCsvModelContainers;

}
