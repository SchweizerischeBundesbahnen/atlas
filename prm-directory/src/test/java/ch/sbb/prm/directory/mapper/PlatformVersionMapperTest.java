package ch.sbb.prm.directory.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.BasicAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.prm.directory.entity.PlatformVersion;
import org.junit.jupiter.api.Test;

class PlatformVersionMapperTest {

  @Test
  void shouldInitToBeCompletedIfNullOnReduced() {
    PlatformVersion platformVersion = new PlatformVersion();
    PlatformVersionMapper.initDefaultDropdownData(platformVersion, true);

    assertThat(platformVersion.getTactileSystem()).isEqualTo(BooleanOptionalAttributeType.TO_BE_COMPLETED);
    assertThat(platformVersion.getVehicleAccess()).isEqualTo(VehicleAccessAttributeType.TO_BE_COMPLETED);
    assertThat(platformVersion.getInfoOpportunities()).containsExactly(InfoOpportunityAttributeType.TO_BE_COMPLETED);
  }

  @Test
  void shouldInitToBeCompletedIfNullOnComplete() {
    PlatformVersion platformVersion = new PlatformVersion();
    PlatformVersionMapper.initDefaultDropdownData(platformVersion, false);

    assertThat(platformVersion.getTactileSystem()).isNull();
    assertThat(platformVersion.getVehicleAccess()).isNull();

    assertThat(platformVersion.getContrastingAreas()).isEqualTo(BooleanOptionalAttributeType.TO_BE_COMPLETED);
    assertThat(platformVersion.getBoardingDevice()).isEqualTo(BoardingDeviceAttributeType.TO_BE_COMPLETED);
    assertThat(platformVersion.getDynamicAudio()).isEqualTo(BasicAttributeType.TO_BE_COMPLETED);
    assertThat(platformVersion.getDynamicVisual()).isEqualTo(BasicAttributeType.TO_BE_COMPLETED);
    assertThat(platformVersion.getLevelAccessWheelchair()).isEqualTo(BasicAttributeType.TO_BE_COMPLETED);
  }
}