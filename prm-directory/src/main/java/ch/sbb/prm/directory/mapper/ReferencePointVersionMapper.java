package ch.sbb.prm.directory.mapper;

import ch.sbb.atlas.api.prm.model.referencepoint.ReadReferencePointVersionModel;
import ch.sbb.atlas.api.prm.model.referencepoint.ReferencePointVersionModel;
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
        .additionalInformation(version.getAdditionalInformation())
        .mainReferencePoint(version.isMainReferencePoint())
        .referencePointType(version.getReferencePointType())
        .creator(version.getCreator())
        .creationDate(version.getCreationDate())
        .editor(version.getEditor())
        .editionDate(version.getEditionDate())
        .etagVersion(version.getVersion())
        .build();
  }

  public static ReferencePointVersion toEntity(ReferencePointVersionModel model){
    return ReferencePointVersion.builder()
        .id(model.getId())
        .sloid(model.getSloid())
        .parentServicePointSloid(model.getParentServicePointSloid())
        .number(new Sloid(model.getParentServicePointSloid()).getServicePointNumber())
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo())
        .designation(model.getDesignation())
        .additionalInformation(model.getAdditionalInformation())
        .mainReferencePoint(model.isMainReferencePoint())
        .referencePointType(model.getReferencePointType())
        .creator(model.getCreator())
        .creationDate(model.getCreationDate())
        .editor(model.getEditor())
        .editionDate(model.getEditionDate())
        .version(model.getEtagVersion())
        .build();
  }

}
