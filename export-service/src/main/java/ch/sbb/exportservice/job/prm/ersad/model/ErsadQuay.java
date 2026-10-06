package ch.sbb.exportservice.job.prm.ersad.model;

import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.LevelAccessWheelchairAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;

public record ErsadQuay(
    String sloid,
    String designation,
    BoardingDeviceAttributeType boardingDevice,
    String adviceAccessInfo,
    BooleanOptionalAttributeType contrastingAreas,
    LevelAccessWheelchairAttributeType levelAccessWheelchair,
    Double superelevation,
    BooleanOptionalAttributeType shuttle,
    VehicleAccessAttributeType vehicleAccess) {

}
