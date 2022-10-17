package ch.sbb.atlas.kafka.model.workflow;

import ch.sbb.atlas.kafka.model.AtlasEvent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowEvent implements AtlasEvent {

  private Long workflowId;
}
