package ch.sbb.atlas.servicepointdirectory.service.servicepoint.bulk;

import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.imports.bulk.AttributeNullingNotSupportedException;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel.Fields;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServicePointBulkImportUpdate extends GeolocationBulkImportDataMapper {

  public void applyNulling(List<String> attributesToNull, UpdateServicePointVersionModel updateModel) {
    for (String attributeToNull : attributesToNull) {
      switch (attributeToNull) {
        case Fields.designationLong -> updateModel.setDesignationLong(null);
        case Fields.stopPointType -> updateModel.setStopPointType(null);
        case Fields.operatingPointType -> updateModel.setOperatingPointType(null);
        case Fields.operatingPointTechnicalTimetableType -> updateModel.setOperatingPointTechnicalTimetableType(null);
        case Fields.meansOfTransport -> updateModel.setMeansOfTransport(null);
        case Fields.categories -> updateModel.setCategories(null);
        case Fields.operatingPointTrafficPointType -> updateModel.setOperatingPointTrafficPointType(null);
        case Fields.sortCodeOfDestinationStation -> updateModel.setSortCodeOfDestinationStation(null);
        case Fields.height -> {
          if (updateModel.getServicePointGeolocation() != null) {
            updateModel.getServicePointGeolocation().setHeight(null);
          }
        }
        case Fields.east, Fields.north, Fields.spatialReference -> updateModel.setServicePointGeolocation(null);
        default -> throw new AttributeNullingNotSupportedException(attributeToNull);
      }
    }
  }

  public UpdateServicePointVersionModel applyUpdateFromCsv(ServicePointVersion currentVersion,
      ServicePointUpdateCsvModel update) {
    UpdateServicePointVersionModel updateModel = new UpdateServicePointVersionModel();

    setNonUpdatableValues(currentVersion, updateModel);

    updateModel.setValidFrom(update.getValidFrom());
    updateModel.setValidTo(update.getValidTo());

    applyValueWithDefault(update.getDesignationOfficial(), currentVersion.getDesignationOfficial(),
        updateModel::setDesignationOfficial);

    applyValueWithDefault(update.getDesignationLong(), currentVersion.getDesignationLong(), updateModel::setDesignationLong);

    applyValueWithDefault(update.getStopPointType(), currentVersion.getStopPointType(), updateModel::setStopPointType);

    applyValueWithDefault(update.getFreightServicePoint(), currentVersion.isFreightServicePoint(),
        updateModel::setFreightServicePoint);
    applyValueWithDefault(update.getOperatingPointType(), currentVersion.getOperatingPointType(),
        updateModel::setOperatingPointType);
    applyValueWithDefault(update.getOperatingPointTechnicalTimetableType(),
        currentVersion.getOperatingPointTechnicalTimetableType(), updateModel::setOperatingPointTechnicalTimetableType);

    updateModel.setMeansOfTransport(
        new ArrayList<>(Optional.ofNullable(update.getMeansOfTransport()).orElse(currentVersion.getMeansOfTransport())));
    updateModel.setCategories(
        new ArrayList<>(Optional.ofNullable(update.getCategories()).orElse(currentVersion.getCategories())));

    applyValueWithDefault(update.getOperatingPointTrafficPointType(),
        currentVersion.getOperatingPointTrafficPointType(), updateModel::setOperatingPointTrafficPointType);
    applyValueWithDefault(update.getSortCodeOfDestinationStation(),
        currentVersion.getSortCodeOfDestinationStation(), updateModel::setSortCodeOfDestinationStation);
    applyValueWithDefault(update.getBusinessOrganisation(), currentVersion.getBusinessOrganisation(),
        updateModel::setBusinessOrganisation);

    updateModel.setServicePointGeolocation(applyGeolocationUpdate(currentVersion.getServicePointGeolocation(), update));
    return updateModel;
  }

  private static void setNonUpdatableValues(ServicePointVersion currentVersion, UpdateServicePointVersionModel updateModel) {
    updateModel.setId(currentVersion.getId());
    updateModel.setEtagVersion(currentVersion.getVersion());

    updateModel.setAbbreviation(currentVersion.getAbbreviation());

    updateModel.setOperatingPointRouteNetwork(currentVersion.isOperatingPointRouteNetwork());
    updateModel.setOperatingPointKilometerMasterNumber(currentVersion.getOperatingPointKilometerMaster() == null ? null :
        currentVersion.getOperatingPointKilometerMaster().getNumber());
  }

}
