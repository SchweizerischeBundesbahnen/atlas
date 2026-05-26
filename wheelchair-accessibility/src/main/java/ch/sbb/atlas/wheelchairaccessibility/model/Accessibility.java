package ch.sbb.atlas.wheelchairaccessibility.model;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.model.DateRange;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class Accessibility {

  private final List<AccessibilityInfo> accessibilityInfos = new ArrayList<>();

  public void add(DateRange dateRange, WheelchairAccessibilityState wheelchairAccessibilityState) {
    accessibilityInfos.add(new AccessibilityInfo(dateRange, wheelchairAccessibilityState));
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (AccessibilityInfo accessibilityInfo : accessibilityInfos) {
      builder.append(accessibilityInfo.toString());
      builder.append(System.lineSeparator());
    }
    return builder.toString();
  }

  @RequiredArgsConstructor
  static class AccessibilityInfo {

    private final DateRange dateRange;
    private final WheelchairAccessibilityState accessibilityState;

    @Override
    public String toString() {
      return AccessibilityRanges.DATE_TIME_FORMATTER.format(dateRange.getFrom())
          + "-"
          + AccessibilityRanges.DATE_TIME_FORMATTER.format(dateRange.getTo())
          + ": "
          + accessibilityState.name();
    }
  }

}
