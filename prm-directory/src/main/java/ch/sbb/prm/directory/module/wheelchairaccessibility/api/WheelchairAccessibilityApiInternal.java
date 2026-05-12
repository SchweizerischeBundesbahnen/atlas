package ch.sbb.prm.directory.module.wheelchairaccessibility.api;

import ch.sbb.atlas.annotation.UnauthorizedAllowed;
import ch.sbb.atlas.annotation.UnauthorizedAllowed.FurtherLimitations;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.ReadWheelchairAccessibilityModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Wheelchair Accessibility")
@RequestMapping("internal/wheelchair-accessibility/{sloid}")
@Validated
public interface WheelchairAccessibilityApiInternal {

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @GetMapping("platforms")
  ReadWheelchairAccessibilityModel getPlatformAccessibilityToday(@PathVariable String sloid);

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @GetMapping("stop-points")
  ReadWheelchairAccessibilityModel getStopPointAccessibilityToday(@PathVariable String sloid);

}
