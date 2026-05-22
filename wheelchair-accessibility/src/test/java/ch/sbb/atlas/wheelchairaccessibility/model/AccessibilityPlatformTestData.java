package ch.sbb.atlas.wheelchairaccessibility.model;

import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.LevelAccessWheelchairAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AccessibilityPlatformTestData implements AccessibilityPlatform {

  private final BooleanOptionalAttributeType shuttle;
  private final VehicleAccessAttributeType vehicleAccess;
  private final LevelAccessWheelchairAttributeType levelAccessWheelchair;
  private final BoardingDeviceAttributeType boardingDevice;
  private final Double superelevation;
  private final String sloid;
  private final LocalDate validFrom;
  private final LocalDate validTo;
}
