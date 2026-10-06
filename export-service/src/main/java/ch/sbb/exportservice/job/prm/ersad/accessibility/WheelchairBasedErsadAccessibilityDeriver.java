package ch.sbb.exportservice.job.prm.ersad.accessibility;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.wheelchairaccessibility.calculator.WheelchairAccessibility;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRequest;
import ch.sbb.exportservice.job.prm.ersad.model.NetexTriState;
import ch.sbb.exportservice.job.prm.platform.entity.PlatformVersion;
import ch.sbb.exportservice.job.prm.relation.entity.RelationVersion;
import ch.sbb.exportservice.job.prm.stoppoint.entity.StopPointVersion;
import ch.sbb.exportservice.job.prm.wheelchairaccessibility.service.AccessibilityRequestFactory;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class WheelchairBasedErsadAccessibilityDeriver implements ErsadAccessibilityDeriver {

  @Override
  public NetexTriState derive(StopPointVersion stopPoint, List<PlatformVersion> platforms,
      List<RelationVersion> relations, LocalDate date) {
    AccessibilityRequest requestOnDate = AccessibilityRequestFactory.forStopPoint(List.of(stopPoint), platforms, relations)
        .getRequestOnDate(date);
    return toNetex(WheelchairAccessibility.calculateStopPointOnDate(requestOnDate));
  }

  static NetexTriState toNetex(WheelchairAccessibilityState state) {
    return switch (state) {
      case AUTONOMY -> NetexTriState.TRUE;
      case RAMP_USE, PRE_REGISTRATION, SHUTTLE -> NetexTriState.PARTIAL;
      case NO_ACCESS -> NetexTriState.FALSE;
      case NO_INFO -> NetexTriState.UNKNOWN;
    };
  }
}
