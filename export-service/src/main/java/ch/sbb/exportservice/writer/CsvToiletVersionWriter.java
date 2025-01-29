package ch.sbb.exportservice.writer;

import ch.sbb.atlas.export.model.prm.BasePrmCsvModel;
import ch.sbb.atlas.export.model.prm.ToiletVersionCsvModel;
import ch.sbb.atlas.export.model.prm.ToiletVersionCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvToiletVersionWriter extends BaseCsvWriter<ToiletVersionCsvModel> {

  @Override
  String[] getCsvHeader() {
    return new String[]{
        Fields.sloid, Fields.parentSloidServicePoint, Fields.parentNumberServicePoint, Fields.designation,
        Fields.additionalInformation, Fields.wheelchairToilet,
        BasePrmCsvModel.Fields.validFrom, BasePrmCsvModel.Fields.validTo, BasePrmCsvModel.Fields.creationDate,
        BasePrmCsvModel.Fields.editionDate, BasePrmCsvModel.Fields.status
    };
  }

}
