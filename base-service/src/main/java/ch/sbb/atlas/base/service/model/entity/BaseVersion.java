package ch.sbb.atlas.base.service.model.entity;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.base.service.model.validation.DatesValidator;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
public abstract class BaseVersion extends BaseEntity implements DatesValidator {

  @NotNull
  @Enumerated(EnumType.STRING)
  private Status status;

}
