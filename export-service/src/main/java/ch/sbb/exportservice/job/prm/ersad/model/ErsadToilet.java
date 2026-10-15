package ch.sbb.exportservice.job.prm.ersad.model;

import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;

public record ErsadToilet(String sloid, String designation, StandardAttributeType wheelchairToilet, String version) {

}
