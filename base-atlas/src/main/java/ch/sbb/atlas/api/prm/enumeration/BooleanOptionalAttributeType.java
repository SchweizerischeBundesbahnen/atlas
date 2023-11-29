package ch.sbb.atlas.api.prm.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true, example = "YES")
@Getter
@RequiredArgsConstructor
public enum BooleanOptionalAttributeType {
  TO_BE_COMPLETED(0),
  YES(1),
  NO(2);

  private final Integer rank;

  public static BooleanOptionalAttributeType of(Integer value) {
    if (value == null) {
      return null;
    }
    return Stream.of(values()).filter(i -> i.getRank().equals(value)).findFirst().orElseThrow();
  }

}
