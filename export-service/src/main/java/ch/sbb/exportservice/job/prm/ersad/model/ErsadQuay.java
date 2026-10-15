package ch.sbb.exportservice.job.prm.ersad.model;

import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.LevelAccessWheelchairAttributeType;

public record ErsadQuay(
    String sloid,
    String designation,
    BoardingDeviceAttributeType boardingDevice,
    String adviceAccessInfo,
    BooleanOptionalAttributeType contrastingAreas,
    LevelAccessWheelchairAttributeType levelAccessWheelchair,
    Double superelevation,
    BooleanOptionalAttributeType shuttle,
    String version) {

}
