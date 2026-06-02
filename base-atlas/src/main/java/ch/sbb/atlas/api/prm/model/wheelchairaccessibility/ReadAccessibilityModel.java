package ch.sbb.atlas.api.prm.model.wheelchairaccessibility;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
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

  @NotNull
  private List<AccessibilityRow> rows;

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  @Builder
  @Schema(name = "AccessibilityRow")
  public static class AccessibilityRow {

    @NotNull
    private LocalDate from;

    @NotNull
    private LocalDate to;

    @NotNull
    private WheelchairAccessibilityState accessibilityState;

  }

}
