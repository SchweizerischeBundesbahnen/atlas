package ch.sbb.atlas.api.prm.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true, example = "YES")
@Getter
@RequiredArgsConstructor
// Rank from https://code.sbb.ch/projects/PT_ABLDIDOK/repos/didokfrontend/browse/src/app/pages/behig/models/behig-form.ts#11
public enum StandardAttributeType {
  TO_BE_COMPLETED(0),
  YES(1),
  NO(2),
  NOT_APPLICABLE(3),
  PARTIALLY(4);

  private final Integer rank;

  public static StandardAttributeType from(Integer rank) {
    Stream<StandardAttributeType> stream = Arrays.stream(StandardAttributeType.values());
    return stream.filter(standardAttributeType -> standardAttributeType.getRank().equals(rank))
        .findFirst().orElseThrow(() -> new IllegalArgumentException("You have to provide one of the following value: " + stream.map(
            StandardAttributeType::getRank)));
  }

}
