package ch.sbb.prm.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BasicAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.RecordingStatus;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.entity.PlatformVersion;
import org.junit.jupiter.api.Test;

class PlatformRecordingStatusEvaluatorTest {

  @Test
  void shouldCalculateReducedPlatformToComplete() {
    PlatformVersion platformVersion = PlatformTestData.getReducedPlatformVersion();
    platformVersion.setTactileSystem(BooleanOptionalAttributeType.YES);

    RecordingStatus result = PlatformRecordingStatusEvaluator.getStatusForPlatform(platformVersion, true);
    assertThat(result).isEqualTo(RecordingStatus.COMPLETE);
  }

  @Test
  void shouldCalculateReducedPlatformToIncomplete() {
    PlatformVersion platformVersion = PlatformTestData.getReducedPlatformVersion();
    platformVersion.setTactileSystem(BooleanOptionalAttributeType.TO_BE_COMPLETED);

    RecordingStatus result = PlatformRecordingStatusEvaluator.getStatusForPlatform(platformVersion, true);
    assertThat(result).isEqualTo(RecordingStatus.INCOMPLETE);
  }

  @Test
  void shouldCalculateCompletePlatformToComplete() {
    PlatformVersion platformVersion = PlatformTestData.getCompletePlatformVersion();
    platformVersion.setDynamicVisual(BasicAttributeType.NO);

    RecordingStatus result = PlatformRecordingStatusEvaluator.getStatusForPlatform(platformVersion, false);
    assertThat(result).isEqualTo(RecordingStatus.COMPLETE);
  }

  @Test
  void shouldCalculateCompletePlatformToIncomplete() {
    PlatformVersion platformVersion = PlatformTestData.getCompletePlatformVersion();
    platformVersion.setBoardingDevice(BoardingDeviceAttributeType.TO_BE_COMPLETED);

    RecordingStatus result = PlatformRecordingStatusEvaluator.getStatusForPlatform(platformVersion, false);
    assertThat(result).isEqualTo(RecordingStatus.INCOMPLETE);
  }
}