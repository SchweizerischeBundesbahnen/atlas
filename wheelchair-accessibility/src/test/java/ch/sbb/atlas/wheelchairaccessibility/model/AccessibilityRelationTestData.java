package ch.sbb.atlas.wheelchairaccessibility.model;

import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.versioning.model.VersioningData;
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

  public static AccessibilityRelationTestDataBuilder buildAutonomyRelation(String sloid) {
    return AccessibilityRelationTestData.builder()
        .sloid(sloid)
        .stepFreeAccess(StepFreeAccessAttributeType.YES)
        .validFrom(VersioningData.MIN_DATE)
        .validTo(VersioningData.MAX_DATE);
  }
}
