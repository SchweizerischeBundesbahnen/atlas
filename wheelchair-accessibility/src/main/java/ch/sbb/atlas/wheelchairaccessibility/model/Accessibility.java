package ch.sbb.atlas.wheelchairaccessibility.model;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.model.DateRange;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Accessibility {

  private final List<AccessibilityInfo> accessibilityInfos = new ArrayList<>();

  public Accessibility with(DateRange dateRange, WheelchairAccessibilityState wheelchairAccessibilityState) {
    accessibilityInfos.add(new AccessibilityInfo(dateRange, wheelchairAccessibilityState));
    return this;
  }

  @ToString
  @EqualsAndHashCode
  @RequiredArgsConstructor
  static class AccessibilityInfo {

    private final DateRange dateRange;
    private final WheelchairAccessibilityState accessibilityState;

  }

}
