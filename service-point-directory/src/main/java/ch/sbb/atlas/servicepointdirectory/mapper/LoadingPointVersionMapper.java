package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.api.servicepoint.LoadingPointVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LoadingPointVersionMapper {

  public static LoadingPointVersionModel fromEntity(LoadingPointVersion loadingPointVersion) {
    return LoadingPointVersionModel.builder()
        .id(loadingPointVersion.getId())
        .number(loadingPointVersion.getNumber())
        .designation(loadingPointVersion.getDesignation())
        .designationLong(loadingPointVersion.getDesignationLong())
        .connectionPoint(loadingPointVersion.isConnectionPoint())
        .servicePointNumber(loadingPointVersion.getServicePointNumber())
        .validFrom(loadingPointVersion.getValidFrom())
        .validTo(loadingPointVersion.getValidTo())
        .loadingPointGeolocation(GeolocationMapper.toModel(loadingPointVersion.getLoadingPointGeolocation()))
        .creationDate(loadingPointVersion.getCreationDate())
        .creator(loadingPointVersion.getCreator())
        .editionDate(loadingPointVersion.getEditionDate())
        .editor(loadingPointVersion.getEditor())
        .build();
  }

}
