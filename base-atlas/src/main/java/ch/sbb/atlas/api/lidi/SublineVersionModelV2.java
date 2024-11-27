package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.lidi.enumaration.SublineConcessionType;
import ch.sbb.atlas.validation.DatesValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.ReadOnlyProperty;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldNameConstants
@Schema(name = "SublineVersionV2")
public class SublineVersionModelV2 extends BaseSublineVersionModel implements DatesValidator {

  @Schema(description = "SublineConcessionType")
  @ReadOnlyProperty
  private SublineConcessionType sublineConcessionType;

}
