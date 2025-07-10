package ch.sbb.atlas.api.prm.model.platform;

import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@FieldNameConstants
@Schema(name = "TerminatePlatform")
public class TerminatePlatformModel {

  @NotNull
  @AtlasVersionableProperty
  private LocalDate validTo;
}
