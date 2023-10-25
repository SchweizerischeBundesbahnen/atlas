package ch.sbb.prm.directory.mapper;

import ch.sbb.atlas.api.prm.model.relation.CreateRelationVersionModel;
import ch.sbb.atlas.api.prm.model.relation.ReadRelationVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.entity.RelationVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RelationVersionMapper {

  public static ReadRelationVersionModel toModel(RelationVersion version){
    return ReadRelationVersionModel.builder()
        .id(version.getId())
        .sloid(version.getSloid())
        .referencePointSloid(version.getReferencePointSloid())
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

  public static RelationVersion toEntity(CreateRelationVersionModel model){
    return RelationVersion.builder()
        .id(model.getId())
        .sloid(model.getSloid())
        .referencePointSloid(model.getReferencePointSloid())
        .parentServicePointSloid(model.getParentServicePointSloid())
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(model.getNumberWithoutCheckDigit()))
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo())
        .tactileVisualMarks(model.getTactileVisualMarks())
        .contrastingAreas(model.getContrastingAreas())
        .stepFreeAccess(model.getStepFreeAccess())
        .referencePointElementType(model.getReferencePointElementType())
        .creator(model.getCreator())
        .creationDate(model.getCreationDate())
        .editor(model.getEditor())
        .editionDate(model.getEditionDate())
        .version(model.getEtagVersion())
        .build();
  }

}
