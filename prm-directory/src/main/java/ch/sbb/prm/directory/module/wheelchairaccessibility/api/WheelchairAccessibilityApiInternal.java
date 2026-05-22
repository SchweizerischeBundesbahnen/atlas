package ch.sbb.prm.directory.module.wheelchairaccessibility.api;

import ch.sbb.atlas.annotation.UnauthorizedAllowed;
import ch.sbb.atlas.annotation.UnauthorizedAllowed.FurtherLimitations;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.ReadWheelchairAccessibilityModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Wheelchair Accessibility")
@RequestMapping("internal/wheelchair-accessibility")
public interface WheelchairAccessibilityApiInternal {

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @GetMapping("{platformSloid}/platform")
  ReadWheelchairAccessibilityModel getPlatformAccessibilityToday(@PathVariable String platformSloid);

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @GetMapping("{stopPointSloid}/stop-point")
  ReadWheelchairAccessibilityModel getStopPointAccessibilityToday(@PathVariable String stopPointSloid);

}
