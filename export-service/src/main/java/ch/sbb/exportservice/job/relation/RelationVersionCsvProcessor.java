package ch.sbb.exportservice.job.relation;

import ch.sbb.atlas.export.model.prm.RelationVersionCsvModel;
import ch.sbb.exportservice.job.BaseServicePointProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class RelationVersionCsvProcessor implements
    ItemProcessor<RelationVersion, RelationVersionCsvModel> {

  @Override
  public RelationVersionCsvModel process(RelationVersion version) {
    return RelationVersionCsvModel.builder()
        .elementSloid(version.getSloid())
        .parentSloidServicePoint(version.getParentServicePointSloid())
        .parentNumberServicePoint(version.getParentServicePointNumber().getNumber())
        .referencePointSloid(version.getReferencePointSloid())
        .tactileVisualMarks(version.getTactileVisualMarks())
        .contrastingAreas(version.getContrastingAreas())
        .stepFreeAccess(version.getStepFreeAccess())
        .referencePointElementType(version.getReferencePointElementType())
        .validFrom(BaseServicePointProcessor.DATE_FORMATTER.format(version.getValidFrom()))
        .validTo(BaseServicePointProcessor.DATE_FORMATTER.format(version.getValidTo()))
        .creationDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
        .editionDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(version.getEditionDate()))
        .build();
  }

}
