package ch.sbb.atlas.workflow.model;

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
public abstract class BaseVersionSnapshot implements AtlasVersionSnapshoatble {

  @NotNull
  private Long workflowId;

  @NotNull
  private Long parentObjectId;

}
