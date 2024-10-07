package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import static ch.sbb.atlas.servicepointdirectory.service.GeolocationUpdateUtility.applyGeolocationUpdates;
import static ch.sbb.atlas.servicepointdirectory.service.GeolocationUpdateUtility.applyUpdateIfValueNotNull;
import static ch.sbb.atlas.servicepointdirectory.service.GeolocationUpdateUtility.geolocationValuesAreNull;

import ch.sbb.atlas.imports.bulk.AttributeNullingNotSupportedException;
import ch.sbb.atlas.imports.bulk.TrafficPointUpdateCsvModel;
import ch.sbb.atlas.imports.bulk.TrafficPointUpdateCsvModel.Fields;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.TrafficPointElementGeolocation;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TrafficPointBulkImportUpdate {

  public void applyNulling(List<String> attributesToNull, TrafficPointElementVersion editedVersion) {
    for (String attributeToNull : attributesToNull) {
      switch (attributeToNull) {
        case Fields.designation -> editedVersion.setDesignation(null);
        case Fields.designationOperational -> editedVersion.setDesignationOperational(null);
        case Fields.length -> editedVersion.setLength(null);
        case Fields.boardingAreaHeight -> editedVersion.setBoardingAreaHeight(null);
        case Fields.compassDirection -> editedVersion.setCompassDirection(null);
        case Fields.height -> {
          if (editedVersion.hasGeolocation()) {
            editedVersion.getTrafficPointElementGeolocation().setHeight(null);
          }
        }
        case Fields.east, Fields.north, Fields.spatialReference -> editedVersion.setTrafficPointElementGeolocation(null);
        case Fields.parentSloid -> editedVersion.setParentSloid(null);
        default -> throw new AttributeNullingNotSupportedException(attributeToNull);
      }
    }
  }

  public TrafficPointElementVersion applyUpdateFromCsv(TrafficPointElementVersion currentVersion,
      TrafficPointUpdateCsvModel update) {
    TrafficPointElementVersion editedVersion = currentVersion.toBuilder().build();
    editedVersion.setValidFrom(update.getValidFrom());
    editedVersion.setValidTo(update.getValidTo());

    applyUpdateIfValueNotNull(update.getDesignation(), editedVersion::setDesignation);
    applyUpdateIfValueNotNull(update.getDesignationOperational(), editedVersion::setDesignationOperational);
    applyUpdateIfValueNotNull(update.getLength(), editedVersion::setLength);
    applyUpdateIfValueNotNull(update.getBoardingAreaHeight(), editedVersion::setBoardingAreaHeight);
    applyUpdateIfValueNotNull(update.getCompassDirection(), editedVersion::setCompassDirection);
    applyUpdateIfValueNotNull(update.getParentSloid(), editedVersion::setParentSloid);

    if (geolocationValuesAreNull(editedVersion.getTrafficPointElementGeolocation(), update)) {
      return editedVersion;
    }
    TrafficPointElementGeolocation trafficPointElementGeolocation = editedVersion.getTrafficPointElementGeolocation() == null ?
        new TrafficPointElementGeolocation() : editedVersion.getTrafficPointElementGeolocation().toBuilder().build();

    applyGeolocationUpdates(update, trafficPointElementGeolocation);

    editedVersion.setTrafficPointElementGeolocation(trafficPointElementGeolocation);
    return editedVersion;
  }

}
