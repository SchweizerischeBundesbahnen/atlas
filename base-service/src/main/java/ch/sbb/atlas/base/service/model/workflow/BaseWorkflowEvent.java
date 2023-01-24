package ch.sbb.atlas.base.service.model.workflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public abstract class BaseWorkflowEvent {

  private Long workflowId;

  private Long businessObjectId;

  private WorkflowStatus workflowStatus;

  private String sbbUserId;

}
