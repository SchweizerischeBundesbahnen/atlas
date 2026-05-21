package ch.sbb.atlas.wheelchairaccessibility.model;

import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.LevelAccessWheelchairAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;

public interface AccessibilityPlatform extends AccessibilityVersion {

  BooleanOptionalAttributeType getShuttle();

  VehicleAccessAttributeType getVehicleAccess();

  LevelAccessWheelchairAttributeType getLevelAccessWheelchair();

  BoardingDeviceAttributeType getBoardingDevice();

  Double getSuperelevation();

}
