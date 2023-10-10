package ch.sbb.prm.directory.mapper;

import ch.sbb.prm.directory.controller.model.relation.RelationVersionModel;
import ch.sbb.prm.directory.entity.RelationVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RelationVersionMapper {

  public static RelationVersionModel toModel(RelationVersion version){
    return RelationVersionModel.builder()
        .id(version.getId())
        .sloid(version.getSloid())
        .parentServicePointSloid(version.getParentServicePointSloid())
        .number(version.getNumber())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .tactileVisualMarks(version.getTactileVisualMarks())
        .contrastingAreas(version.getContrastingAreas())
        .stepFreeAccess(version.getStepFreeAccess())
        .referencePointElementType(version.getReferencePointElementType())
        .creator(version.getCreator())
        .creationDate(version.getCreationDate())
        .editor(version.getEditor())
        .editionDate(version.getEditionDate())
        .etagVersion(version.getVersion())
        .build();
  }

}
