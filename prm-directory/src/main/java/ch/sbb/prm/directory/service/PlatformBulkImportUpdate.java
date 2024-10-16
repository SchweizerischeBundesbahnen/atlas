package ch.sbb.prm.directory.service;

import static ch.sbb.atlas.imports.util.BulkImportUtility.applyUpdateIfValueNotNull;

import ch.sbb.atlas.imports.bulk.AttributeNullingNotSupportedException;
import ch.sbb.atlas.imports.bulk.PlatformUpdateCsvModel;
import ch.sbb.atlas.imports.bulk.PlatformUpdateCsvModel.Fields;
import ch.sbb.prm.directory.entity.PlatformVersion;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PlatformBulkImportUpdate {

  public void applyNulling(List<String> attributesToNull, PlatformVersion editedVersion) {
    for (String attributeToNull : attributesToNull) {
      switch (attributeToNull) {
        case Fields.additionalInformation -> editedVersion.setAdditionalInformation(null);
        case Fields.height -> editedVersion.setHeight(null);
        case Fields.inclinationLongitudinal -> editedVersion.setInclinationLongitudinal(null);
        case Fields.infoOpportunities -> editedVersion.setInfoOpportunities(null);
        case Fields.partialElevation -> editedVersion.setPartialElevation(null);
        case Fields.tactileSystem -> editedVersion.setTactileSystem(null);
        case Fields.vehicleAccess -> editedVersion.setVehicleAccess(null);
        case Fields.wheelchairAreaLength -> editedVersion.setWheelchairAreaLength(null);
        case Fields.wheelchairAreaWidth -> editedVersion.setWheelchairAreaWidth(null);
        default -> throw new AttributeNullingNotSupportedException(attributeToNull);
      }
    }
  }

  public PlatformVersion applyUpdateFromCsv(PlatformVersion currentVersion, PlatformUpdateCsvModel update) {
    PlatformVersion editedVersion = currentVersion.toBuilder().build();
    editedVersion.setValidFrom(update.getValidFrom());
    editedVersion.setValidTo(update.getValidTo());

    applyUpdateIfValueNotNull(update.getAdditionalInformation(), editedVersion::setAdditionalInformation);
    applyUpdateIfValueNotNull(update.getHeight(), editedVersion::setHeight);
    applyUpdateIfValueNotNull(update.getInclinationLongitudinal(), editedVersion::setInclinationLongitudinal);
    applyUpdateIfValueNotNull(update.getInfoOpportunities(), editedVersion::setInfoOpportunities);
    applyUpdateIfValueNotNull(update.getPartialElevation(), editedVersion::setPartialElevation);
    applyUpdateIfValueNotNull(update.getTactileSystem(), editedVersion::setTactileSystem);
    applyUpdateIfValueNotNull(update.getVehicleAccess(), editedVersion::setVehicleAccess);
    applyUpdateIfValueNotNull(update.getWheelchairAreaLength(), editedVersion::setWheelchairAreaLength);
    applyUpdateIfValueNotNull(update.getWheelchairAreaWidth(), editedVersion::setWheelchairAreaWidth);

    editedVersion.setParentServicePointSloid(currentVersion.getParentServicePointSloid());
    return editedVersion;
  }

}
