package ch.sbb.prm.directory.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true, example = "YES")
@Getter
@RequiredArgsConstructor
public enum StandardPrmAttributeType {
  TO_BE_COMPLETED(0),
  YES(1),
  NO(2),
  NOT_APPLICABLE(3),
  PARTIALLY(4);

  private final Integer rank;

}
