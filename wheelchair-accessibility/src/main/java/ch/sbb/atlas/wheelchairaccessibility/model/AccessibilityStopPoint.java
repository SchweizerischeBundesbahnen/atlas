package ch.sbb.atlas.wheelchairaccessibility.model;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import java.time.LocalDate;

public interface AccessibilityStopPoint {

  StandardAttributeType getAlternativeTransport();

  StandardAttributeType getAssistanceService();

  StandardAttributeType getAssistanceAvailability();

  BooleanOptionalAttributeType getAssistanceRequestFulfilled();

  boolean isReduced();

  LocalDate getValidFrom();

  LocalDate getValidTo();

}
