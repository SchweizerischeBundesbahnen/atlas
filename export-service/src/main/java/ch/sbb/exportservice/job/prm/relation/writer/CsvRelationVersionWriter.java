package ch.sbb.exportservice.job.prm.relation.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.job.BaseCsvWriter;
import ch.sbb.exportservice.job.prm.relation.model.RelationVersionCsvModel;
import ch.sbb.exportservice.job.prm.relation.model.RelationVersionCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvRelationVersionWriter extends BaseCsvWriter<RelationVersionCsvModel> {

  CsvRelationVersionWriter(FileService fileService) {
    super(fileService);
  }

  @Override
  protected String[] getCsvHeader() {
    return new String[]{
        Fields.elementSloid, Fields.parentSloidServicePoint, Fields.parentNumberServicePoint, Fields.referencePointSloid,
        Fields.tactileVisualMarks, Fields.contrastingAreas, Fields.stepFreeAccess, Fields.referencePointElementType,
        Fields.validFrom, Fields.validTo, Fields.creationDate, Fields.editionDate
    };
  }

}
