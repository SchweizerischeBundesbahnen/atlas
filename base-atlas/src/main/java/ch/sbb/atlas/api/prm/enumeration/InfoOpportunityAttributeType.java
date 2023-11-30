package ch.sbb.atlas.api.prm.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true, example = "STATIC_VISUAL_INFORMATION")
@Getter
@RequiredArgsConstructor
// Ranks from https://code.sbb.ch/projects/PT_ABLDIDOK/repos/didokfrontend/browse/src/app/pages/behig/models/behig-form.ts
public enum InfoOpportunityAttributeType {

  TO_BE_COMPLETED(0),
  STATIC_VISUAL_INFORMATION(15),
  ELECTRONIC_VISUAL_INFORMATION_DEPARTURES(16),
  ELECTRONIC_VISUAL_INFORMATION_COMPLETE(17),
  ACOUSTIC_INFORMATION(18),
  TEXT_TO_SPEECH_DEPARTURES(19),
  TEXT_TO_SPEECH_COMPLETE(20);

  private final Integer rank;

  public static InfoOpportunityAttributeType of(Integer value) {
    return Stream.of(values()).filter(i -> i.getRank().equals(value)).findFirst().orElseThrow();
  }

}
