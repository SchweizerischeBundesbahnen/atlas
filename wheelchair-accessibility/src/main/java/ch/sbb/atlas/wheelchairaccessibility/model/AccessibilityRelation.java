package ch.sbb.atlas.wheelchairaccessibility.model;

import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import java.time.LocalDate;

public interface AccessibilityRelation {

  StepFreeAccessAttributeType getStepFreeAccess();

  LocalDate getValidFrom();

  LocalDate getValidTo();
}
