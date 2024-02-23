package ch.sbb.exportservice.processor;

import ch.sbb.atlas.export.model.prm.RelationVersionCsvModel;
import ch.sbb.exportservice.entity.RelationVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class RelationVersionCsvProcessor implements
    ItemProcessor<RelationVersion, RelationVersionCsvModel> {

  @Override
  public RelationVersionCsvModel process(RelationVersion version) {
    return RelationVersionCsvModel.builder()
        .sloid(version.getSloid())
        .parentSloidServicePoint(version.getParentServicePointSloid())
        .parentNumberServicePoint(version.getParentServicePointNumber().getNumber())
        .referencePointSloid(version.getReferencePointSloid())
        .tactileVisualMarks(version.getTactileVisualMarks().toString())
        .contrastingAreas(version.getContrastingAreas().toString())
        .stepFreeAccess(version.getStepFreeAccess().toString())
        .referencePointElementType(version.getReferencePointElementType().toString())
        .validFrom(BaseServicePointProcessor.DATE_FORMATTER.format(version.getValidFrom()))
        .validTo(BaseServicePointProcessor.DATE_FORMATTER.format(version.getValidTo()))
        .creationDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
        .editionDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(version.getEditionDate()))
        .build();
  }

}
