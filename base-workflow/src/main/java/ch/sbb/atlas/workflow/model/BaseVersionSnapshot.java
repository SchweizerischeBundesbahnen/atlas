package ch.sbb.atlas.workflow.model;

import ch.sbb.atlas.kafka.model.workflow.model.WorkflowStatus;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public abstract class BaseVersionSnapshot implements AtlasVersionSnapshotable {

  @NotNull
  private Long workflowId;

  /**
   * The DB id of the snapshotted entity Version, used to create the mapping between Version and SnapshottedVersion
   */
  @NotNull
  private Long parentObjectId;

  @NotNull
  @Enumerated(EnumType.STRING)
  private WorkflowStatus workflowStatus;

}
