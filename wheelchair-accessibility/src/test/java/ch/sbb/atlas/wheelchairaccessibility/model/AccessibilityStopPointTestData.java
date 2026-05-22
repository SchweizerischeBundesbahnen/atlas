package ch.sbb.atlas.wheelchairaccessibility.model;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AccessibilityStopPointTestData implements AccessibilityStopPoint {

  private final StandardAttributeType alternativeTransport;
  private final StandardAttributeType assistanceService;
  private final StandardAttributeType assistanceAvailability;
  private final BooleanOptionalAttributeType assistanceRequestFulfilled;
  private final boolean reduced;
  private final String sloid;
  private final LocalDate validFrom;
  private final LocalDate validTo;
}
