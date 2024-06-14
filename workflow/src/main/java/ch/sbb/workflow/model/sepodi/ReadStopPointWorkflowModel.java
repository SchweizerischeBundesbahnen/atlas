package ch.sbb.workflow.model.sepodi;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Schema(name = "ReadStopPointWorkflow")
public class ReadStopPointWorkflowModel extends BaseStopPointWorkflowModel {

  @Schema(description = "Workflow start date")
  private LocalDate startDate;

  @Schema(description = "Workflow end date")
  private LocalDate endDate;

  @Schema(description = "Object creation date", example = "01.01.2000")
  private LocalDateTime creationDate;

  @Schema(description = "User creator", example = "u123456")
  private String creator;

}
