package ch.sbb.atlas.api.servicepoint;

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
@Schema(name = "StopPointWorkflowTermination")
public class StopPointWorkflowTerminationModel {

  @NotNull
  private String sloid;

  @NotNull
  private Long versionId;

  @Schema(description = "StopPoint Termination date")
  @NotNull
  private LocalDate terminationDate;

}