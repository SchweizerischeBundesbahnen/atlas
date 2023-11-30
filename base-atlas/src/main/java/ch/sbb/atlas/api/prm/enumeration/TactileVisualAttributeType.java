package ch.sbb.atlas.api.prm.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true, example = "WITH_REMOTE_CONTROL")
@Getter
@RequiredArgsConstructor
// Ranks from https://code.sbb.ch/projects/PT_ABLDIDOK/repos/didokfrontend/browse/src/app/pages/behig/models/behig-form.ts#11
public enum TactileVisualAttributeType {

  TO_BE_COMPLETED(0),
  YES(1),
  NO(2),
  NOT_APPLICABLE(3),
  PARTIALLY(4),
  WITH_REMOTE_CONTROL(7);

  private final Integer rank;

}
