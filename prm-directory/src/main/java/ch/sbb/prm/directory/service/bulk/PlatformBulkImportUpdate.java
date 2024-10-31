package ch.sbb.prm.directory.service.bulk;

import ch.sbb.atlas.api.prm.model.platform.PlatformVersionModel;
import ch.sbb.atlas.imports.bulk.AttributeNullingNotSupportedException;
import ch.sbb.atlas.imports.bulk.BulkImportDataMapper;
import ch.sbb.atlas.imports.bulk.PlatformReducedUpdateCsvModel;
import ch.sbb.atlas.imports.bulk.PlatformReducedUpdateCsvModel.Fields;
import ch.sbb.prm.directory.entity.PlatformVersion;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PlatformBulkImportUpdate extends BulkImportDataMapper {

  public void applyNulling(List<String> attributesToNull, PlatformVersionModel updateModel) {
    for (String attributeToNull : attributesToNull) {
      switch (attributeToNull) {
        case Fields.additionalInformation -> updateModel.setAdditionalInformation(null);
        case Fields.height -> updateModel.setHeight(null);
        case Fields.inclinationLongitudinal -> updateModel.setInclinationLongitudinal(null);
        case Fields.infoOpportunities -> updateModel.setInfoOpportunities(null);
        case Fields.partialElevation -> updateModel.setPartialElevation(null);
        case Fields.tactileSystem -> updateModel.setTactileSystem(null);
        case Fields.vehicleAccess -> updateModel.setVehicleAccess(null);
        case Fields.wheelchairAreaLength -> updateModel.setWheelchairAreaLength(null);
        case Fields.wheelchairAreaWidth -> updateModel.setWheelchairAreaWidth(null);
        default -> throw new AttributeNullingNotSupportedException(attributeToNull);
      }
    }
  }

  public PlatformVersionModel applyUpdateFromCsv(PlatformVersion currentVersion, PlatformReducedUpdateCsvModel update) {
    PlatformVersionModel updateModel = new PlatformVersionModel();

    setNonUpdatableValues(currentVersion, updateModel);

    updateModel.setValidFrom(update.getValidFrom());
    updateModel.setValidTo(update.getValidTo());

    applyValueWithDefault(update.getAdditionalInformation(), currentVersion.getAdditionalInformation(),
        updateModel::setAdditionalInformation);
    applyValueWithDefault(update.getHeight(), currentVersion.getHeight(), updateModel::setHeight);
    applyValueWithDefault(update.getInclinationLongitudinal(), currentVersion.getInclinationLongitudinal(),
        updateModel::setInclinationLongitudinal);
    updateModel.setInfoOpportunities(
        new ArrayList<>(Optional.ofNullable(update.getInfoOpportunities()).orElse(currentVersion.getInfoOpportunities())));
    applyValueWithDefault(update.getPartialElevation(), currentVersion.getPartialElevation(), updateModel::setPartialElevation);
    applyValueWithDefault(update.getTactileSystem(), currentVersion.getTactileSystem(), updateModel::setTactileSystem);
    applyValueWithDefault(update.getVehicleAccess(), currentVersion.getVehicleAccess(), updateModel::setVehicleAccess);
    applyValueWithDefault(update.getWheelchairAreaLength(), currentVersion.getWheelchairAreaLength(),
        updateModel::setWheelchairAreaLength);
    applyValueWithDefault(update.getWheelchairAreaWidth(), currentVersion.getWheelchairAreaWidth(),
        updateModel::setWheelchairAreaWidth);

    return updateModel;
  }

  private static void setNonUpdatableValues(PlatformVersion currentVersion, PlatformVersionModel updateModel) {
    updateModel.setId(currentVersion.getId());
    updateModel.setEtagVersion(currentVersion.getVersion());

    updateModel.setParentServicePointSloid(currentVersion.getParentServicePointSloid());
  }

}
