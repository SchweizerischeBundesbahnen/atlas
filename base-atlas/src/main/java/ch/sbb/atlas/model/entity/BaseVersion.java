package ch.sbb.atlas.model.entity;

import ch.sbb.atlas.kafka.model.Status;
import ch.sbb.atlas.validation.DatesValidator;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
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
