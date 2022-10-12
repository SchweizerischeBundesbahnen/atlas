package ch.sbb.atlas.base.service.model.entity;

import ch.sbb.atlas.base.service.model.validation.DatesValidator;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
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
public abstract class BaseVersion extends BaseEntity implements DatesValidator {

  @Version
  @NotNull
  @AtlasVersionableProperty(ignoreDiff = true)
  private Integer version;

}
