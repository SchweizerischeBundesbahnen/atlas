package ch.sbb.atlas.api.prm.model.stoppoint;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@SuperBuilder
@FieldNameConstants
@Schema(name = "RecordingObligationUpdateRequest")
public class RecordingObligationUpdateRequest {

  private Boolean value;

}
