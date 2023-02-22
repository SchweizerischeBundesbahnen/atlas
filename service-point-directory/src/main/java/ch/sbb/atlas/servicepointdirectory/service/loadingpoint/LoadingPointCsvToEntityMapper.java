package ch.sbb.atlas.servicepointdirectory.service.loadingpoint;

import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.LoadingPointGeolocation;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import java.util.function.Function;

public class LoadingPointCsvToEntityMapper implements
    Function<LoadingPointCsvModel, LoadingPointVersion> {

  @Override
  public LoadingPointVersion apply(LoadingPointCsvModel loadingPointCsvModel) {
    LoadingPointVersion loadingPointVersion = LoadingPointVersion
        .builder()
        .number(loadingPointCsvModel.getNumber())
        .designation(loadingPointCsvModel.getDesignation())
        .designationLong(loadingPointCsvModel.getDesignationLong())
        .connectionPoint(loadingPointCsvModel.getConnectionPoint())
        .servicePointNumber(ServicePointNumber.of(loadingPointCsvModel.getServicePointNumber()))
        .validFrom(loadingPointCsvModel.getValidFrom())
        .validTo(loadingPointCsvModel.getValidTo())
        .creator(loadingPointCsvModel.getCreatedBy())
        .creationDate(loadingPointCsvModel.getCreatedAt())
        .editor(loadingPointCsvModel.getEditedBy())
        .editionDate(loadingPointCsvModel.getEditedAt())
        .build();

    LoadingPointGeolocation geolocation = LoadingPointGeolocation
        .builder()
        .spatialReference(loadingPointCsvModel.getSpatialReference())
        .east(loadingPointCsvModel.getOriginalEast())
        .north(loadingPointCsvModel.getOriginalNorth())
        .height(loadingPointCsvModel.getHeight())
        .creator(loadingPointCsvModel.getCreatedBy())
        .creationDate(loadingPointCsvModel.getCreatedAt())
        .editor(loadingPointCsvModel.getEditedBy())
        .editionDate(loadingPointCsvModel.getEditedAt())
        .build();

    if (geolocation.isValid()) {
      loadingPointVersion.setLoadingPointGeolocation(geolocation);
      geolocation.setLoadingPointVersion(loadingPointVersion);
    }

    return loadingPointVersion;

  }
}
