package ch.sbb.prm.directory.mapper;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.controller.model.referencepoint.CreateReferencePointVersionModel;
import ch.sbb.prm.directory.controller.model.referencepoint.ReadReferencePointVersionModel;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReferencePointVersionMapper {

  public static ReadReferencePointVersionModel toModel(ReferencePointVersion version){
    return ReadReferencePointVersionModel.builder()
        .id(version.getId())
        .sloid(version.getSloid())
        .parentServicePointSloid(version.getParentServicePointSloid())
        .number(version.getNumber())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .designation(version.getDesignation())
        .mainReferencePoint(version.isMainReferencePoint())
        .referencePointType(version.getReferencePointType())
        .creator(version.getCreator())
        .creationDate(version.getCreationDate())
        .editor(version.getEditor())
        .editionDate(version.getEditionDate())
        .etagVersion(version.getVersion())
        .build();
  }

  public static ReferencePointVersion toEntity(CreateReferencePointVersionModel model){
    return ReferencePointVersion.builder()
        .id(model.getId())
        .sloid(model.getSloid())
        .parentServicePointSloid(model.getParentServicePointSloid())
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(model.getNumberWithoutCheckDigit()))
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo())
        .designation(model.getDesignation())
        .mainReferencePoint(model.isMainReferencePoint())
        .referencePointType(model.getReferencePointType())
        .creator(model.getCreator())
        .creationDate(model.getCreationDate())
        .editor(model.getEditor())
        .editionDate(model.getEditionDate())
        .build();
  }

}
