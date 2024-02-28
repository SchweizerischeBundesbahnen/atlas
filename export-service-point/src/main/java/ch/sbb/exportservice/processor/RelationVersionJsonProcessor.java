package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.prm.model.relation.ReadRelationVersionModel;
import ch.sbb.exportservice.entity.RelationVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class RelationVersionJsonProcessor extends BaseServicePointProcessor implements ItemProcessor<RelationVersion,
    ReadRelationVersionModel> {

  @Override
  public ReadRelationVersionModel process(RelationVersion version) {
    return ReadRelationVersionModel.builder()
        .id(version.getId())
        .elementSloid(version.getSloid())
        .number(version.getParentServicePointNumber())
        .parentServicePointSloid(version.getParentServicePointSloid())
        .referencePointSloid(version.getReferencePointSloid())
        .tactileVisualMarks(version.getTactileVisualMarks())
        .contrastingAreas(version.getContrastingAreas())
        .stepFreeAccess(version.getStepFreeAccess())
        .referencePointElementType(version.getReferencePointElementType())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .creationDate(version.getCreationDate())
        .creator(version.getCreator())
        .editionDate(version.getEditionDate())
        .editor(version.getEditor())
        .etagVersion(version.getVersion())
        .build();
  }

}
