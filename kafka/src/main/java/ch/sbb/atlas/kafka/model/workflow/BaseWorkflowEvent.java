package ch.sbb.atlas.kafka.model.workflow;

import ch.sbb.atlas.kafka.model.workflow.event.AtlasEvent;
import ch.sbb.atlas.kafka.model.workflow.model.WorkflowStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public abstract class BaseWorkflowEvent implements Serializable, AtlasEvent {

  @Serial
  private static final long serialVersionUID = 1;

  private Long workflowId;

  private Long businessObjectId;

  private WorkflowStatus workflowStatus;

}
