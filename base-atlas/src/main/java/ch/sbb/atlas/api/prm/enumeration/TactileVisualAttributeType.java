package ch.sbb.atlas.api.prm.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true, example = "WITH_REMOTE_CONTROL")
@Getter
@RequiredArgsConstructor
public enum TactileVisualAttributeType {

  TO_BE_COMPLETED(0),
  YES(1),
  NO(2),
  NOT_APPLICABLE(3),
  PARTIALLY(4),
  WITH_REMOTE_CONTROL(7);

  private final Integer rank;

}
