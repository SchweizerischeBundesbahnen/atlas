package ch.sbb.atlas.api.prm.model.relation;

import ch.sbb.atlas.validation.DatesValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Schema(name = "CreateRelationVersion")
public class CreateRelationVersionModel extends RelationVersionModel implements DatesValidator {

}
