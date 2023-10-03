package ch.sbb.prm.directory.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true, example = "YES")
@Getter
@RequiredArgsConstructor
public enum InfoOpportunityType {

  TO_BE_COMPLETED(0),
  STATIC_VISUAL_INFORMATION(15),
  ELECTRONIC_VISUAL_INFORMATION_DEPARTURES(16),
  ELECTRONIC_VISUAL_INFORMATION_COMPLETE(17),
  ACOUSTIC_INFORMATION(18),
  TEXT_TO_SPEECH_DEPARTURES(19),
  TEXT_TO_SPEECH_COMPLETE(20);

  private final Integer rank;

}
