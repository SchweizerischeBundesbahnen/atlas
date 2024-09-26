package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel.Fields;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.ServicePointGeolocation;
import java.util.List;
import java.util.function.Consumer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServicePointBulkImportUpdate {

  public void applyNulling(List<String> attributesToNull, ServicePointVersion editedVersion) {
    for (String attributeToNull : attributesToNull) {
      switch (attributeToNull) {
        case Fields.designationLong -> editedVersion.setDesignationLong(null);
        case Fields.stopPointType -> editedVersion.setStopPointType(null);
        case Fields.operatingPointType -> editedVersion.setOperatingPointType(null);
        case Fields.operatingPointTechnicalTimetableType -> editedVersion.setOperatingPointTechnicalTimetableType(null);
        case Fields.meansOfTransport -> editedVersion.setMeansOfTransport(null);
        case Fields.categories -> editedVersion.setCategories(null);
        case Fields.operatingPointTrafficPointType -> editedVersion.setOperatingPointTrafficPointType(null);
        case Fields.sortCodeOfDestinationStation -> editedVersion.setSortCodeOfDestinationStation(null);
        case Fields.height -> {
          if (editedVersion.hasGeolocation()) {
            editedVersion.getServicePointGeolocation().setHeight(null);
          }
        }
        case Fields.east, Fields.north, Fields.spatialReference -> editedVersion.setServicePointGeolocation(null);
        default -> throw new UnsupportedOperationException("Field " + attributeToNull + " not supported for attribute nulling");
      }
    }
  }

  public ServicePointVersion applyUpdateFromCsv(ServicePointVersion currentVersion, ServicePointUpdateCsvModel update) {
    ServicePointVersion editedVersion = currentVersion.toBuilder().build();
    editedVersion.setValidFrom(update.getValidFrom());
    editedVersion.setValidTo(update.getValidTo());

    applyUpdateIfValueNotNull(update.getDesignationOfficial(), editedVersion::setDesignationOfficial);
    applyUpdateIfValueNotNull(update.getDesignationLong(), editedVersion::setDesignationLong);

    applyUpdateIfValueNotNull(update.getStopPointType(), editedVersion::setStopPointType);

    applyUpdateIfValueNotNull(update.getFreightServicePoint(), editedVersion::setFreightServicePoint);
    applyUpdateIfValueNotNull(update.getOperatingPointType(), editedVersion::setOperatingPointType);
    applyUpdateIfValueNotNull(update.getOperatingPointTechnicalTimetableType(), editedVersion::setOperatingPointTechnicalTimetableType);

    applyUpdateIfValueNotNull(update.getMeansOfTransport(), editedVersion::setMeansOfTransport);
    applyUpdateIfValueNotNull(update.getCategories(), editedVersion::setCategories);

    applyUpdateIfValueNotNull(update.getOperatingPointTrafficPointType(), editedVersion::setOperatingPointTrafficPointType);
    applyUpdateIfValueNotNull(update.getSortCodeOfDestinationStation(), editedVersion::setSortCodeOfDestinationStation);
    applyUpdateIfValueNotNull(update.getBusinessOrganisation(), editedVersion::setBusinessOrganisation);

    if (editedVersion.getServicePointGeolocation() == null
        && update.getNorth() == null && update.getEast() == null && update.getSpatialReference() == null) {
      return editedVersion;
    }

    ServicePointGeolocation servicePointGeolocation =
        editedVersion.getServicePointGeolocation() == null ? new ServicePointGeolocation()
            : editedVersion.getServicePointGeolocation().toBuilder().build();
    applyUpdateIfValueNotNull(update.getNorth(), servicePointGeolocation::setNorth);
    applyUpdateIfValueNotNull(update.getEast(), servicePointGeolocation::setEast);
    applyUpdateIfValueNotNull(update.getSpatialReference(), servicePointGeolocation::setSpatialReference);

    applyUpdateIfValueNotNull(update.getHeight(), servicePointGeolocation::setHeight);

    editedVersion.setServicePointGeolocation(servicePointGeolocation);
    return editedVersion;
  }

  private <T> void applyUpdateIfValueNotNull(T value, Consumer<T> setterFunction) {
    if (value != null) {
      setterFunction.accept(value);
    }
  }

}
