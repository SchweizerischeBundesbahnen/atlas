package ch.sbb.atlas.kafka.model.workflow.event;

import ch.sbb.atlas.kafka.model.workflow.BaseWorkflowEvent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class LineWorkflowEvent extends BaseWorkflowEvent implements Serializable {

  @Serial
  private static final long serialVersionUID = 1;

}
