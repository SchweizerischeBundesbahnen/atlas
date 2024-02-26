package ch.sbb.prm.directory.mapper;

import static ch.sbb.atlas.api.prm.enumeration.StandardAttributeType.TO_BE_COMPLETED;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.RecordingStatus;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.prm.directory.ToiletTestData;
import ch.sbb.prm.directory.entity.ToiletVersion;
import org.junit.jupiter.api.Test;

class ToiletVersionMapperTest {

  @Test
  void shouldReturnIncompleteStatus() {
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    toiletVersion.setWheelchairToilet(TO_BE_COMPLETED);

    RecordingStatus result = ToiletVersionMapper.getRecordingStatus(toiletVersion);
    assertThat(result).isEqualTo(RecordingStatus.INCOMPLETE);
  }

  @Test
  void shouldReturnCompleteStatus() {
    ToiletVersion toiletVersion = ToiletTestData.getToiletVersion();
    toiletVersion.setWheelchairToilet(StandardAttributeType.YES);

    RecordingStatus result = ToiletVersionMapper.getRecordingStatus(toiletVersion);
    assertThat(result).isEqualTo(RecordingStatus.COMPLETE);
  }

}