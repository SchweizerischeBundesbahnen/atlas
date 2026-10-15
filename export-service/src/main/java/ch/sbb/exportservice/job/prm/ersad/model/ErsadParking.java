package ch.sbb.exportservice.job.prm.ersad.model;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;

public record ErsadParking(
    String sloid,
    String designation,
    BooleanOptionalAttributeType placesAvailable,
    BooleanOptionalAttributeType prmPlacesAvailable,
    String version) {

}
