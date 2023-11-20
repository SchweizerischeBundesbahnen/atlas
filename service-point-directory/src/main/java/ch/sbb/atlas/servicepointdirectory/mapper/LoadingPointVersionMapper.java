package ch.sbb.atlas.servicepointdirectory.mapper;

import ch.sbb.atlas.api.servicepoint.CreateLoadingPointVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadLoadingPointVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LoadingPointVersionMapper {

  public static ReadLoadingPointVersionModel fromEntity(LoadingPointVersion loadingPointVersion) {
    return ReadLoadingPointVersionModel.builder()
        .id(loadingPointVersion.getId())
        .number(loadingPointVersion.getNumber())
        .designation(loadingPointVersion.getDesignation())
        .designationLong(loadingPointVersion.getDesignationLong())
        .connectionPoint(loadingPointVersion.isConnectionPoint())
        .servicePointNumber(loadingPointVersion.getServicePointNumber())
        .validFrom(loadingPointVersion.getValidFrom())
        .validTo(loadingPointVersion.getValidTo())
        .creationDate(loadingPointVersion.getCreationDate())
        .creator(loadingPointVersion.getCreator())
        .editionDate(loadingPointVersion.getEditionDate())
        .editor(loadingPointVersion.getEditor())
        .etagVersion(loadingPointVersion.getVersion())
        .build();
  }

  public static LoadingPointVersion toEntity(CreateLoadingPointVersionModel model) {
    return LoadingPointVersion.builder()
        .id(model.getId())
        .number(model.getNumber())
        .designation(model.getDesignation())
        .designationLong(model.getDesignationLong())
        .connectionPoint(model.isConnectionPoint())
        .servicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(model.getServicePointNumber()))
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo())
        .version(model.getEtagVersion())
        .editor(model.getEditor())
        .editionDate(model.getEditionDate())
        .creator(model.getCreator())
        .creationDate(model.getCreationDate())
        .build();
  }
}
