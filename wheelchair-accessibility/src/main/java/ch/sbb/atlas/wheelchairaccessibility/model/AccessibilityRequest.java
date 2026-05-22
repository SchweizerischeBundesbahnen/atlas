package ch.sbb.atlas.wheelchairaccessibility.model;

import ch.sbb.atlas.model.DateRange;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessibilityRequest {

  @Builder.Default
  private final List<? extends AccessibilityStopPoint> stopPoint = new ArrayList<>();
  @Builder.Default
  private final List<? extends AccessibilityPlatform> platform = new ArrayList<>();
  @Builder.Default
  private final List<? extends AccessibilityRelation> relations = new ArrayList<>();

  public List<DateRange> getAllDateRanges() {
    return Stream.of(stopPoint, platform, relations)
        .flatMap(Collection::stream)
        .map(AccessibilityVersion::toDateRange)
        .toList();
  }

  public AccessibilityRequest getRequestOnDate(LocalDate date) {
    return AccessibilityRequest.builder()
        .stopPoint(stopPoint.stream().filter(i -> i.toDateRange().contains(date)).toList())
        .platform(platform.stream().filter(i -> i.toDateRange().contains(date)).toList())
        .relations(relations.stream().filter(i -> i.toDateRange().contains(date)).toList())
        .build();
  }

}
