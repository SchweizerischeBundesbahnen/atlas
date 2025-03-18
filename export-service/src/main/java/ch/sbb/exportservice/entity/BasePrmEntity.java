package ch.sbb.exportservice.entity;

import ch.sbb.atlas.model.Status;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@MappedSuperclass
@FieldNameConstants
public abstract class BasePrmEntity extends BaseEntity {

  private Status status;

}
