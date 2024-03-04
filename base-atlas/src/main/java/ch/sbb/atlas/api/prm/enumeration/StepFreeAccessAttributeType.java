package ch.sbb.atlas.api.prm.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Schema(enumAsRef = true, example = "YES_WITH_LIFT")
@Getter
@RequiredArgsConstructor
// Ranks from https://code.sbb.ch/projects/PT_ABLDIDOK/repos/didokfrontend/browse/src/app/pages/behig/models/behig-form.ts#11
public enum StepFreeAccessAttributeType {

  TO_BE_COMPLETED(0),
  YES(1),
  NO(2),
  NOT_APPLICABLE(3),
  YES_WITH_LIFT(5),
  YES_WITH_RAMP(6);

  private final Integer rank;

  public static StepFreeAccessAttributeType of(Integer value) {
    if (value == null) {
      return null;
    }
    return Stream.of(values()).filter(i -> i.getRank().equals(value)).findFirst().orElseThrow();
  }
}
