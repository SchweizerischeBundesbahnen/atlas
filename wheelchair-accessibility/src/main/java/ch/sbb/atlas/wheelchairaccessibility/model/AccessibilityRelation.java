package ch.sbb.atlas.wheelchairaccessibility.model;

import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;

public interface AccessibilityRelation extends AccessibilityVersion {

  StepFreeAccessAttributeType getStepFreeAccess();

}