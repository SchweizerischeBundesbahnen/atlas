package ch.sbb.atlas.api.prm.model.wheelchairaccessibility;

import ch.sbb.atlas.model.DateRange;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "ReadAccessibility")
public class ReadAccessibilityModel {

  private List<AccessibilityRow> rows;

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  @Builder
  @Schema(name = "AccessibilityRow")
  public static class AccessibilityRow {

    private DateRange dateRange;
    private WheelchairAccessibilityState accessibilityState;

  }

}
