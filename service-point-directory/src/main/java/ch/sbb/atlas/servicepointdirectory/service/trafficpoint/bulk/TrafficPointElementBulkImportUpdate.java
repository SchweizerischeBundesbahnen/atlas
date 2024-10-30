package ch.sbb.atlas.servicepointdirectory.service.trafficpoint.bulk;

import ch.sbb.atlas.api.servicepoint.CreateTrafficPointElementVersionModel;
import ch.sbb.atlas.imports.bulk.AttributeNullingNotSupportedException;
import ch.sbb.atlas.imports.bulk.TrafficPointUpdateCsvModel;
import ch.sbb.atlas.imports.bulk.TrafficPointUpdateCsvModel.Fields;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.bulk.GeolocationBulkImportDataMapper;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TrafficPointElementBulkImportUpdate extends GeolocationBulkImportDataMapper {

  public void applyNulling(List<String> attributesToNull, CreateTrafficPointElementVersionModel updateModel) {
    for (String attributeToNull : attributesToNull) {
      switch (attributeToNull) {
        case Fields.designation -> updateModel.setDesignation(null);
        case Fields.designationOperational -> updateModel.setDesignationOperational(null);
        case Fields.length -> updateModel.setLength(null);
        case Fields.boardingAreaHeight -> updateModel.setBoardingAreaHeight(null);
        case Fields.compassDirection -> updateModel.setCompassDirection(null);
        case Fields.height -> {
          if (updateModel.getTrafficPointElementGeolocation() != null) {
            updateModel.getTrafficPointElementGeolocation().setHeight(null);
          }
        }
        case Fields.east, Fields.north, Fields.spatialReference -> updateModel.setTrafficPointElementGeolocation(null);
        case Fields.parentSloid -> updateModel.setParentSloid(null);
        default -> throw new AttributeNullingNotSupportedException(attributeToNull);
      }
    }
  }

  public CreateTrafficPointElementVersionModel applyUpdateFromCsv(TrafficPointElementVersion currentVersion,
      TrafficPointUpdateCsvModel update) {
    CreateTrafficPointElementVersionModel updateModel = new CreateTrafficPointElementVersionModel();

    setNonUpdatableValues(currentVersion, updateModel);

    updateModel.setValidFrom(update.getValidFrom());
    updateModel.setValidTo(update.getValidTo());

    applyValueWithDefault(update.getDesignation(), currentVersion.getDesignation(), updateModel::setDesignation);
    applyValueWithDefault(update.getDesignationOperational(), currentVersion.getDesignationOperational(),
        updateModel::setDesignationOperational);

    applyValueWithDefault(update.getLength(), currentVersion.getLength(), updateModel::setLength);
    applyValueWithDefault(update.getBoardingAreaHeight(), currentVersion.getBoardingAreaHeight(),
        updateModel::setBoardingAreaHeight);
    applyValueWithDefault(update.getCompassDirection(), currentVersion.getCompassDirection(), updateModel::setCompassDirection);
    applyValueWithDefault(update.getParentSloid(), currentVersion.getParentSloid(), updateModel::setParentSloid);

    updateModel.setTrafficPointElementGeolocation(applyGeolocationUpdate(currentVersion.getTrafficPointElementGeolocation(), update));
    return updateModel;
  }

  private static void setNonUpdatableValues(TrafficPointElementVersion currentVersion,
      CreateTrafficPointElementVersionModel updateModel) {
    updateModel.setId(currentVersion.getId());
    updateModel.setEtagVersion(currentVersion.getVersion());

    updateModel.setNumberWithoutCheckDigit(currentVersion.getServicePointNumber().getNumber());
    updateModel.setSloid(currentVersion.getSloid());
  }

}
