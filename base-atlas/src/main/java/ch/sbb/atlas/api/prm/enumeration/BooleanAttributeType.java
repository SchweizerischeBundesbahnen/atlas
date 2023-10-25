package ch.sbb.atlas.api.prm.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true, example = "YES")
@Getter
@RequiredArgsConstructor
public enum BooleanAttributeType {
  YES(1),
  NO(2);

  private final Integer rank;

}
