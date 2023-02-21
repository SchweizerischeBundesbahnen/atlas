package ch.sbb.atlas.workflow.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@MappedSuperclass
public abstract class BaseWorkflowEntity {

  @Enumerated(EnumType.STRING)
  private WorkflowProcessingStatus workflowProcessingStatus;

  private Long workflowId;

}
