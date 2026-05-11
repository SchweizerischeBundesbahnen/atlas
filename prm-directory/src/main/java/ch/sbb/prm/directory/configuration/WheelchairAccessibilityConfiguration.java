package ch.sbb.prm.directory.configuration;

import ch.sbb.atlas.wheelchairaccessibility.calculator.PlatformCompleteAccessibilityCalculator;
import ch.sbb.atlas.wheelchairaccessibility.calculator.PlatformReducedAccessibilityCalculator;
import ch.sbb.atlas.wheelchairaccessibility.calculator.StopPointCompleteAccessibilityCalculator;
import ch.sbb.atlas.wheelchairaccessibility.combiner.PlatformCompleteAccessibilityCombiner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WheelchairAccessibilityConfiguration {

  @Bean
  public PlatformReducedAccessibilityCalculator platformReducedAccessibilityCalculator() {
    return new PlatformReducedAccessibilityCalculator();
  }

  @Bean
  public PlatformCompleteAccessibilityCalculator platformCompleteAccessibilityCalculator() {
    return new PlatformCompleteAccessibilityCalculator();
  }

  @Bean
  public StopPointCompleteAccessibilityCalculator stopPointCompleteAccessibilityCalculator() {
    return new StopPointCompleteAccessibilityCalculator();
  }

  @Bean
  public PlatformCompleteAccessibilityCombiner platformCompleteAccessibilityCombiner() {
    return new PlatformCompleteAccessibilityCombiner();
  }

}
