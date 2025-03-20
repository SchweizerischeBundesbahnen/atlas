package ch.sbb.exportservice.job.prm.contactpoint.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.job.BaseCsvWriter;
import ch.sbb.exportservice.job.prm.BasePrmCsvModel;
import ch.sbb.exportservice.job.prm.contactpoint.model.ContactPointVersionCsvModel;
import org.springframework.stereotype.Component;

@Component
public class CsvContactPointVersionWriter extends BaseCsvWriter<ContactPointVersionCsvModel> {

  CsvContactPointVersionWriter(FileService fileService) {
    super(fileService);
  }

  @Override
  protected String[] getCsvHeader() {
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
