package ch.sbb.prm.directory.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true, example = "YES")
@Getter
@RequiredArgsConstructor
public enum BooleanPrmAttributeType {
  YES(1),
  NO(2);

  private final Integer rank;

}
