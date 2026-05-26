package ch.sbb.atlas.wheelchairaccessibility.model;

import ch.sbb.atlas.model.DateRange;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@Builder
@AllArgsConstructor
public class AccessibilityRanges implements Iterable<DateRange> {

  @Builder.Default
  private final List<DateRange> dateRanges = new ArrayList<>();

  @Override
  public Iterator<DateRange> iterator() {
    return dateRanges.iterator();
  }
}
