package ch.sbb.prm.directory.module.wheelchairaccessibility.calculator;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;
import org.springframework.stereotype.Component;

@Component
public class PlatformCompleteAccessibilityCalculator implements PlatformAccessibilityCalculator {

  @Override
  public WheelchairAccessibilityState calculate(PlatformVersion platformVersion) {
    throw new UnsupportedOperationException();
  }
}
