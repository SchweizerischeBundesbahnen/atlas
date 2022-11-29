package ch.sbb.atlas.workflow.model;

import ch.sbb.atlas.base.service.model.entity.BaseVersion;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
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
public abstract class BaseVersionSnapshot extends BaseVersion {

  @NotNull
  private Long workflowId;

  @NotNull
  private Long parentObjectId;

}
