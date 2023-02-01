package ch.sbb.atlas.base.service.model.workflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class WorkflowEvent {

  @NotNull
  private Long workflowId;

  @NotNull
  private Long businessObjectId;

  @NotNull
  private WorkflowType workflowType;

  private WorkflowStatus workflowStatus;

}
