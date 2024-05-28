package ch.sbb.workflow.model.sepodi;

import ch.sbb.atlas.kafka.model.SwissCanton;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Schema(name = "StopPointAddWorkflow")
public class StopPointAddWorkflowModel extends BaseStopPointWorkflowModel{

  @NotNull
  @Schema(description = "Canton, the statement is for")
  private SwissCanton swissCanton;

}
