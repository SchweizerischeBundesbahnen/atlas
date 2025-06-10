package ch.sbb.workflow.sepodi.termination.model;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@Schema(name = "TerminationInfo")
public class TerminationInfoModel {

  @Schema(description = "Workflow id", accessMode = AccessMode.READ_ONLY)
  private Long workflowId;

  @Schema(description = "Termination Date defined for the Stop Point")
  private LocalDate terminationDate;

}
