package ch.sbb.exportservice.job.prm.ersad.model;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;

public record ErsadAssistance(
    StandardAttributeType assistanceAvailability,
    String assistanceCondition,
    String assistanceService,
    BooleanOptionalAttributeType assistanceRequestFulfilled,
    StandardAttributeType alternativeTransport,
    String alternativeTransportCondition) {

  public boolean isAssistanceOffered() {
    return assistanceAvailability == StandardAttributeType.YES
        || assistanceAvailability == StandardAttributeType.PARTIALLY;
  }
}
