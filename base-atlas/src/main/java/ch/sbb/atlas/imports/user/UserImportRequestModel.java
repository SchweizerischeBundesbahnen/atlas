package ch.sbb.atlas.imports.user;

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
@Schema(name = "UserImportRequest")
public class UserImportRequestModel {

  @Schema(name = "List of UserCsvModel to import")
  @NotNull
  @NotEmpty
  private List<UserCsvModel> userCsvModels;

}
