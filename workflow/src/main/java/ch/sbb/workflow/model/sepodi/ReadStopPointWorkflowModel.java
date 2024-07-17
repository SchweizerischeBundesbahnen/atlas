package ch.sbb.workflow.model.sepodi;

import ch.sbb.atlas.workflow.model.WorkflowStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
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

  @Schema(description = "Designation Official")
  private String designationOfficial;

  @Schema(description = "Locality Name")
  private String localityName;

  @Schema(description = "Workflow Status", accessMode = AccessMode.READ_ONLY)
  private WorkflowStatus status;

  @Schema(description = "Service Point version valid from")
  private LocalDate versionValidFrom;

  @Schema(description = "Service Point version valid to")
  private LocalDate versionValidTo;

  @Schema(description = "Service Point version SBOID")
  private String sboid;

  @Schema(description = "Previous Workflow id")
  private Long previousWorkflowId;

  @Schema(description = "Workflow created at")
  private LocalDateTime createdAt;

  @Schema(description = "Workflow start date", example = "01.01.2000", accessMode = AccessMode.READ_ONLY)
  private LocalDate startDate;

  @Schema(description = "Workflow end date")
  private LocalDate endDate;

  @Schema(description = "Object creation date", example = "01.01.2000")
  private LocalDateTime creationDate;

  @Schema(description = "User creator", example = "u123456", accessMode = AccessMode.READ_ONLY)
  private String creator;

}
