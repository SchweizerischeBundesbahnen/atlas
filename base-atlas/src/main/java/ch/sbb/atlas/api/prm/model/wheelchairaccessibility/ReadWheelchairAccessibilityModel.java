package ch.sbb.atlas.api.prm.model.wheelchairaccessibility;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "ReadWheelchairAccessibility")
public class ReadWheelchairAccessibilityModel {

  private WheelchairAccessibilityState state;

}
