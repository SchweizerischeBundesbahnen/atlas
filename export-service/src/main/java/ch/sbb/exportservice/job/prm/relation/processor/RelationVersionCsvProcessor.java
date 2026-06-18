package ch.sbb.exportservice.job.prm.relation.processor;

import static ch.sbb.atlas.helper.DateHelper.DATE_FORMATTER_BASE;
import static ch.sbb.exportservice.util.MapperUtil.LOCAL_DATE_TIME_FORMATTER;

import ch.sbb.exportservice.job.prm.relation.entity.RelationVersion;
import ch.sbb.exportservice.job.prm.relation.model.RelationVersionCsvModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;

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
        .validFrom(DATE_FORMATTER_BASE.format(version.getValidFrom()))
        .validTo(DATE_FORMATTER_BASE.format(version.getValidTo()))
        .creationDate(LOCAL_DATE_TIME_FORMATTER.format(version.getCreationDate()))
        .editionDate(LOCAL_DATE_TIME_FORMATTER.format(version.getEditionDate()))
        .build();
  }

}
