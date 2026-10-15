package ch.sbb.exportservice.job.prm.ersad.model;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;

public record ErsadStationEquipment(
    StandardAttributeType visualInfo,
    StandardAttributeType dynamicOpticSystem,
    StandardAttributeType dynamicAudioSystem,
    BooleanOptionalAttributeType ticketMachine,
    StandardAttributeType wheelchairTicketMachine,
    StandardAttributeType audioTicketMachine) {

}
