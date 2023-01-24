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
public abstract class BaseWorkflowEvent {

  @NotNull
  private Long workflowId;

  @NotNull
  private Long businessObjectId;

  private WorkflowStatus workflowStatus;

}
