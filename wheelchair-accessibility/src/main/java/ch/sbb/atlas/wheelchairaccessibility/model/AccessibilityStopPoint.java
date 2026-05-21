package ch.sbb.atlas.wheelchairaccessibility.model;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;

public interface AccessibilityStopPoint extends AccessibilityVersion {

  StandardAttributeType getAlternativeTransport();

  StandardAttributeType getAssistanceService();

  StandardAttributeType getAssistanceAvailability();

  BooleanOptionalAttributeType getAssistanceRequestFulfilled();

  boolean isReduced();

}
