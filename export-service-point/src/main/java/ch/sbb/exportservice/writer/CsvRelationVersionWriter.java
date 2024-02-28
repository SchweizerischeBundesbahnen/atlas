package ch.sbb.exportservice.writer;

import ch.sbb.atlas.export.model.prm.RelationVersionCsvModel.Fields;
import ch.sbb.atlas.export.model.prm.RelationVersionCsvModel;
import org.springframework.stereotype.Component;

@Component
public class CsvRelationVersionWriter extends BaseCsvWriter<RelationVersionCsvModel> {

  @Override
  String[] getCsvHeader() {
    return new String[]{
        Fields.elementSloid, Fields.parentSloidServicePoint, Fields.parentNumberServicePoint, Fields.referencePointSloid,
        Fields.tactileVisualMarks, Fields.contrastingAreas, Fields.stepFreeAccess, Fields.referencePointElementType,
        Fields.validFrom, Fields.validTo, Fields.creationDate, Fields.editionDate
    };
  }

}
