package ch.sbb.exportservice.job.prm.ersad.model;

import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;

public record ErsadContactPoint(
    String sloid,
    ContactPointType type,
    String designation,
    String openingHours,
    StandardAttributeType wheelchairAccess,
    StandardAttributeType inductionLoop,
    Integer version) {

}
