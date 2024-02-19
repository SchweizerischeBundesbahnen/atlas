package ch.sbb.atlas.api.prm.model.parkinglot;

import ch.sbb.atlas.api.prm.enumeration.RecordingStatus;
import ch.sbb.atlas.validation.DatesValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldNameConstants
@Schema(name = "ParkingLotOverview")
public class ParkingLotOverviewModel extends ParkingLotVersionModel implements DatesValidator {

  @NotNull
  private RecordingStatus recordingStatus;

}
