package ch.sbb.atlas.wheelchairaccessibility.model;

import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AccessibilityRelationTestData implements AccessibilityRelation {

  private final StepFreeAccessAttributeType stepFreeAccess;
  private final String sloid;
  private final LocalDate validFrom;
  private final LocalDate validTo;
}
