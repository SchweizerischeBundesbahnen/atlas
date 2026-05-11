package ch.sbb.atlas.wheelchairaccessibility.model;

import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AccessibilityRelation {

  private final StepFreeAccessAttributeType stepFreeAccess;

}
