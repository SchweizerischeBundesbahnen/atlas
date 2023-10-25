package ch.sbb.atlas.api.prm.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true, example = "YES_WITH_LIFT")
@Getter
@RequiredArgsConstructor
public enum StepFreeAccessAttributeType {

  TO_BE_COMPLETED(0),
  YES(1),
  NO(2),
  NOT_APPLICABLE(3),
  YES_WITH_LIFT(5),
  YES_WITH_RAMP(5);

  private final Integer rank;

}
