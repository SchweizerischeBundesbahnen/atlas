package ch.sbb.atlas.wheelchairaccessibility.model;

import ch.sbb.atlas.model.DateRange;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AccessibilityRanges {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

  @Builder.Default
  private final List<DateRange> dateRanges = new ArrayList<>();

  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (DateRange dateRange : dateRanges) {
      builder.append(DATE_TIME_FORMATTER.format(dateRange.getFrom()));
      builder.append("-");
      builder.append(DATE_TIME_FORMATTER.format(dateRange.getTo()));
    }
    return builder.toString();
  }

}
