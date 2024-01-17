package ch.sbb.atlas.api.prm.model;

import ch.sbb.atlas.api.prm.model.platform.RecordingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@SuperBuilder
@FieldNameConstants
@Schema(name = "PlatformOverview")
public class PlatformOverviewModel {

  @NotNull
  private String sloid;

  @NotNull
  private LocalDate validFrom;

  @NotNull
  private LocalDate validTo;

  @NotNull
  private RecordingStatus recordingStatus;

}
