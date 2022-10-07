package ch.sbb.atlas.base.service.model.entity;

import ch.sbb.atlas.base.service.model.validation.DatesValidator;
import javax.persistence.MappedSuperclass;
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

}
