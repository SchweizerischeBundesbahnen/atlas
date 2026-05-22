package ch.sbb.atlas.wheelchairaccessibility.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AccessibilityFilter {

  private LocalDate from;
  private int days;

}
