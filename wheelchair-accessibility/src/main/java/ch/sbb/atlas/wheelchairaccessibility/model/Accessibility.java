package ch.sbb.atlas.wheelchairaccessibility.model;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.model.DateRange;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class Accessibility {

  private final List<AccessibilityInfo> accessibilityInfos;

  public Accessibility() {
    this(new ArrayList<>());
  }

  Accessibility(List<AccessibilityInfo> accessibilityInfos) {
    this.accessibilityInfos = accessibilityInfos;
  }

  public Accessibility with(DateRange dateRange, WheelchairAccessibilityState wheelchairAccessibilityState) {
    accessibilityInfos.add(new AccessibilityInfo(dateRange, wheelchairAccessibilityState));
    return this;
  }

  public String prettyPrint() {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN_CH);
    StringBuilder builder = new StringBuilder();
    for (AccessibilityInfo accessibilityInfo : accessibilityInfos) {
      builder.append(dateTimeFormatter.format(accessibilityInfo.dateRange.getFrom()));
      builder.append("-");
      builder.append(dateTimeFormatter.format(accessibilityInfo.dateRange.getTo()));
      builder.append(": ");
      builder.append(accessibilityInfo.accessibilityState.name());
      builder.append(System.lineSeparator());
    }
    return builder.toString();
  }

  @Getter
  @ToString
  @EqualsAndHashCode
  @RequiredArgsConstructor
  public static class AccessibilityInfo {

    private final DateRange dateRange;
    private final WheelchairAccessibilityState accessibilityState;

  }

  public Accessibility minify() {
    if (accessibilityInfos.isEmpty()) {
      return this;
    }
    List<AccessibilityInfo> minified = new ArrayList<>();
    minified.add(accessibilityInfos.getFirst());

    Iterator<AccessibilityInfo> iter = accessibilityInfos.iterator();
    AccessibilityInfo current = iter.next();
    AccessibilityInfo previous = current;

    while (iter.hasNext()) {
      current = iter.next();
      if (previous.accessibilityState == current.accessibilityState) {
        AccessibilityInfo removed = minified.removeLast();
        DateRange mergedRange = new DateRange(removed.dateRange.getFrom(), current.dateRange.getTo());
        minified.add(new AccessibilityInfo(mergedRange, current.accessibilityState));
      } else {
        minified.add(current);
      }
      previous = current;
    }

    return new Accessibility(minified);
  }

}
