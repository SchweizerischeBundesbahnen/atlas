package ch.sbb.prm.directory.module.wheelchairaccessibility.api;

import ch.sbb.atlas.annotation.UnauthorizedAllowed;
import ch.sbb.atlas.annotation.UnauthorizedAllowed.FurtherLimitations;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.ReadAccessibilityModel;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.ReadWheelchairAccessibilityModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Wheelchair Accessibility")
@RequestMapping("internal/wheelchair-accessibility")
public interface WheelchairAccessibilityApiInternal {

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @GetMapping("platform/{platformSloid}/today")
  ReadWheelchairAccessibilityModel getPlatformAccessibilityToday(@PathVariable String platformSloid);

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @GetMapping("platform/{platformSloid}")
  ReadAccessibilityModel getPlatformAccessibility(@PathVariable String platformSloid,
      @RequestParam @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN) LocalDate startingFrom);

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @GetMapping("stop-point/{stopPointSloid}/today")
  ReadWheelchairAccessibilityModel getStopPointAccessibilityToday(@PathVariable String stopPointSloid);

  @UnauthorizedAllowed(limitations = FurtherLimitations.NONE)
  @GetMapping("stop-point/{stopPointSloid}")
  ReadAccessibilityModel getStopPointAccessibility(@PathVariable String stopPointSloid,
      @RequestParam @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN) LocalDate startingFrom);

}
