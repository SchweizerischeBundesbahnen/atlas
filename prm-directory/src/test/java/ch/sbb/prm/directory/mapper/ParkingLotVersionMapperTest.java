package ch.sbb.prm.directory.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.RecordingStatus;
import ch.sbb.prm.directory.ParkingLotTestData;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import org.junit.jupiter.api.Test;

class ParkingLotVersionMapperTest {

  @Test
  void shouldReturnIncompleteStatus() {
    ParkingLotVersion parkingLotVersion = ParkingLotTestData.getParkingLotVersion();

    RecordingStatus result = ParkingLotVersionMapper.getRecordingStatus(parkingLotVersion);
    assertThat(result).isEqualTo(RecordingStatus.INCOMPLETE);
  }

  @Test
  void shouldReturnCompleteStatus() {
    ParkingLotVersion parkingLotVersion = ParkingLotTestData.getParkingLotVersion();
    parkingLotVersion.setPlacesAvailable(BooleanOptionalAttributeType.YES);
    parkingLotVersion.setPrmPlacesAvailable(BooleanOptionalAttributeType.YES);

    RecordingStatus result = ParkingLotVersionMapper.getRecordingStatus(parkingLotVersion);
    assertThat(result).isEqualTo(RecordingStatus.COMPLETE);
  }
}