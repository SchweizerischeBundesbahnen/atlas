package ch.sbb.prm.directory.mapper;

import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.TactileVisualAttributeType;
import ch.sbb.atlas.api.prm.model.relation.ReadRelationVersionModel;
import ch.sbb.atlas.api.prm.model.relation.RelationVersionModel;
import ch.sbb.atlas.location.SloidHelper;
import ch.sbb.prm.directory.entity.RelationVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RelationVersionMapper {

  public static ReadRelationVersionModel toModel(RelationVersion version){
    return ReadRelationVersionModel.builder()
        .id(version.getId())
        .status(version.getStatus())
        .elementSloid(version.getSloid())
        .referencePointSloid(version.getReferencePointSloid())
        .referencePointElementType(version.getReferencePointElementType())
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

  public static RelationVersion toEntity(RelationVersionModel model){
    RelationVersion entity = RelationVersion.builder()
        .id(model.getId())
        .sloid(model.getElementSloid())
        .number(SloidHelper.getServicePointNumber(model.getParentServicePointSloid()))
        .referencePointSloid(model.getReferencePointSloid())
        .referencePointElementType(model.getReferencePointElementType())
        .parentServicePointSloid(model.getParentServicePointSloid())
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo())
        .tactileVisualMarks(model.getTactileVisualMarks())
        .contrastingAreas(model.getContrastingAreas())
        .stepFreeAccess(model.getStepFreeAccess())
        .creator(model.getCreator())
        .creationDate(model.getCreationDate())
        .editor(model.getEditor())
        .editionDate(model.getEditionDate())
        .version(model.getEtagVersion())
        .build();
    if (entity.getContrastingAreas() == null) {
      entity.setContrastingAreas(StandardAttributeType.TO_BE_COMPLETED);
    }
    if (entity.getStepFreeAccess() == null) {
      entity.setStepFreeAccess(StepFreeAccessAttributeType.TO_BE_COMPLETED);
    }
    if (entity.getTactileVisualMarks() == null) {
      entity.setTactileVisualMarks(TactileVisualAttributeType.TO_BE_COMPLETED);
    }
    return entity;
  }

}
