package ch.sbb.prm.directory.mapper;

import ch.sbb.prm.directory.controller.model.ReferencePointVersionModel;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReferencePointVersionMapper {

  public static ReferencePointVersionModel toModel(ReferencePointVersion version){
    return ReferencePointVersionModel.builder()
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

}
