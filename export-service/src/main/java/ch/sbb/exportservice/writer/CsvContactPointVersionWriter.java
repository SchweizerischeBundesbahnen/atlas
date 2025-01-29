package ch.sbb.exportservice.writer;

import ch.sbb.atlas.export.model.prm.BasePrmCsvModel;
import ch.sbb.atlas.export.model.prm.ContactPointVersionCsvModel;
import org.springframework.stereotype.Component;

@Component
public class CsvContactPointVersionWriter extends BaseCsvWriter<ContactPointVersionCsvModel> {

  @Override
  String[] getCsvHeader() {
    return new String[]{
        ContactPointVersionCsvModel.Fields.sloid, ContactPointVersionCsvModel.Fields.parentSloidServicePoint,
        ContactPointVersionCsvModel.Fields.parentNumberServicePoint,
        ContactPointVersionCsvModel.Fields.type, ContactPointVersionCsvModel.Fields.designation,
        ContactPointVersionCsvModel.Fields.additionalInformation,
        ContactPointVersionCsvModel.Fields.inductionLoop, ContactPointVersionCsvModel.Fields.openingHours,
        ContactPointVersionCsvModel.Fields.wheelchairAccess,
        BasePrmCsvModel.Fields.validFrom, BasePrmCsvModel.Fields.validTo, BasePrmCsvModel.Fields.creationDate,
        BasePrmCsvModel.Fields.editionDate, BasePrmCsvModel.Fields.status
    };
  }

}
