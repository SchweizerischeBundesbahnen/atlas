package ch.sbb.atlas.servicepointdirectory.service.loadingpoint;

import ch.sbb.atlas.servicepointdirectory.entity.geolocation.LoadingPointGeolocation;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
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
        .servicePointNumber(loadingPointCsvModel.getServicePointNumber())
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
        .lv03east(loadingPointCsvModel.getELv03())
        .lv03north(loadingPointCsvModel.getNLv03())
        .lv95east(loadingPointCsvModel.getELv95())
        .lv95north(loadingPointCsvModel.getNLv95())
        .wgs84east(loadingPointCsvModel.getEWgs84())
        .wgs84north(loadingPointCsvModel.getNWgs84())
        .wgs84webEast(loadingPointCsvModel.getEWgs84web())
        .wgs84webNorth(loadingPointCsvModel.getNWgs84web())
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
