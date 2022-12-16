package ch.sbb.atlas.servicepointdirectory.service.loading.point;

import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import java.util.function.Function;

public class LoadingPointCsvToEntityMapper implements Function<LoadingPointCsvModel, LoadingPointVersion> {

  @Override
  public LoadingPointVersion apply(LoadingPointCsvModel loadingPointCsvModel) {
    return LoadingPointVersion.builder()
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
  }
}
