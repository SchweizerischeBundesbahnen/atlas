package ch.sbb.prm.directory.service;

import ch.sbb.atlas.api.prm.enumeration.BasicAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.api.prm.model.platform.RecordingStatus;
import ch.sbb.prm.directory.entity.PlatformVersion;

public class PlatformRecordingStatusEvaluator {

  public static RecordingStatus getStatusForPlatform(PlatformVersion platform, boolean reduced) {
    if (reduced) {
      if (platform.getTactileSystem() == BooleanOptionalAttributeType.TO_BE_COMPLETED ||
          platform.getVehicleAccess() == VehicleAccessAttributeType.TO_BE_COMPLETED ||
          platform.getInfoOpportunities().contains(InfoOpportunityAttributeType.TO_BE_COMPLETED)
      ) {
        return RecordingStatus.INCOMPLETE;
      }
      return RecordingStatus.COMPLETE;
    }
    if (platform.getBoardingDevice() == BoardingDeviceAttributeType.TO_BE_COMPLETED ||
        platform.getContrastingAreas() == BooleanOptionalAttributeType.TO_BE_COMPLETED ||
        platform.getDynamicAudio() == BasicAttributeType.TO_BE_COMPLETED ||
        platform.getDynamicVisual() == BasicAttributeType.TO_BE_COMPLETED ||
        platform.getLevelAccessWheelchair() == BasicAttributeType.TO_BE_COMPLETED
    ) {
      return RecordingStatus.INCOMPLETE;
    }
    return RecordingStatus.COMPLETE;
  }

}
