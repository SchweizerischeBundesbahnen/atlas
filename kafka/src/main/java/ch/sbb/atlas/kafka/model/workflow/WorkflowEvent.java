package ch.sbb.atlas.kafka.model.workflow;

import ch.sbb.atlas.kafka.model.AtlasEvent;
import ch.sbb.atlas.kafka.model.workflow.model.WorkflowStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowEvent implements Serializable, AtlasEvent {

  @Serial
  private static final long serialVersionUID = 1;

  private Long workflowId;

  private Long businessObjectId;

  private WorkflowStatus workflowStatus;

}
