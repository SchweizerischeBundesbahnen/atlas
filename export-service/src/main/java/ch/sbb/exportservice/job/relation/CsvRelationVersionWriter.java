package ch.sbb.exportservice.job.relation;

import ch.sbb.atlas.export.model.prm.RelationVersionCsvModel;
import ch.sbb.atlas.export.model.prm.RelationVersionCsvModel.Fields;
import ch.sbb.exportservice.job.BaseCsvWriter;
import org.springframework.stereotype.Component;

@Component
public class CsvRelationVersionWriter extends BaseCsvWriter<RelationVersionCsvModel> {

  @Override
  protected String[] getCsvHeader() {
    return new String[]{
        Fields.elementSloid, Fields.parentSloidServicePoint, Fields.parentNumberServicePoint, Fields.referencePointSloid,
        Fields.tactileVisualMarks, Fields.contrastingAreas, Fields.stepFreeAccess, Fields.referencePointElementType,
        Fields.validFrom, Fields.validTo, Fields.creationDate, Fields.editionDate
    };
  }

}
