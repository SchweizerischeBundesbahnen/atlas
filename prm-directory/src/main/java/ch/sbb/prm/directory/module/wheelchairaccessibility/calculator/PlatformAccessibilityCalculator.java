package ch.sbb.prm.directory.module.wheelchairaccessibility.calculator;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;

public interface PlatformAccessibilityCalculator {

  WheelchairAccessibilityState calculate(PlatformVersion platformVersion);

}
