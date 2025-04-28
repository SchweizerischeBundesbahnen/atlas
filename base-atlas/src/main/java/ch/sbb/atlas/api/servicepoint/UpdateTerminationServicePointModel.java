package ch.sbb.atlas.api.servicepoint;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@FieldNameConstants
@Schema(name = "UpdateTerminationServicePoint")
public class UpdateTerminationServicePointModel {

  @Schema(description = "Indicates if a StopPoint is in a termination hearing. Only for internal development usage!")
  private boolean terminationInProgress;

}