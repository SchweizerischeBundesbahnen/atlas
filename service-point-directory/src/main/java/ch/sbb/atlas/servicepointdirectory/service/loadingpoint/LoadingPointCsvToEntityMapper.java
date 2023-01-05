package ch.sbb.atlas.servicepointdirectory.service.loadingpoint;

import ch.sbb.atlas.servicepointdirectory.entity.geolocation.LoadingPointGeolocation;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.service.util.GeolocationMapperUtil;
import java.util.function.Function;

public class LoadingPointCsvToEntityMapper implements
    Function<LoadingPointCsvModel, LoadingPointVersion> {

  @Override
  public LoadingPointVersion apply(LoadingPointCsvModel loadingPointCsvModel) {
    final LoadingPointVersion loadingPointVersion = LoadingPointVersion
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
        .east(GeolocationMapperUtil.getOriginalEast(
            loadingPointCsvModel.getSpatialReference(),
            loadingPointCsvModel.getEWgs84(),
            loadingPointCsvModel.getEWgs84web(),
            loadingPointCsvModel.getELv95(),
            loadingPointCsvModel.getELv03()
        ))
        .north(GeolocationMapperUtil.getOriginalNorth(
            loadingPointCsvModel.getSpatialReference(),
            loadingPointCsvModel.getNWgs84(),
            loadingPointCsvModel.getNWgs84web(),
            loadingPointCsvModel.getNLv95(),
            loadingPointCsvModel.getNLv03()
        ))
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
