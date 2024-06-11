package ch.sbb.workflow.model.sepodi;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
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

  @Schema(description = "Object creation date", example = "01.01.2000", accessMode = AccessMode.READ_ONLY)
  private LocalDateTime creationDate;

  @Schema(description = "User creator", example = "u123456", accessMode = AccessMode.READ_ONLY)
  private String creator;

}
