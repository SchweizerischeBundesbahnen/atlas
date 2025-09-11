package ch.sbb.exportservice.job.prm.contactpoint.writer;

import static ch.sbb.exportservice.job.prm.BasePrmCsvModel.Fields.creationDate;
import static ch.sbb.exportservice.job.prm.BasePrmCsvModel.Fields.editionDate;
import static ch.sbb.exportservice.job.prm.BasePrmCsvModel.Fields.status;
import static ch.sbb.exportservice.job.prm.BasePrmCsvModel.Fields.validFrom;
import static ch.sbb.exportservice.job.prm.BasePrmCsvModel.Fields.validTo;
import static ch.sbb.exportservice.job.prm.contactpoint.model.ContactPointVersionCsvModel.Fields.additionalInformation;
import static ch.sbb.exportservice.job.prm.contactpoint.model.ContactPointVersionCsvModel.Fields.designation;
import static ch.sbb.exportservice.job.prm.contactpoint.model.ContactPointVersionCsvModel.Fields.inductionLoop;
import static ch.sbb.exportservice.job.prm.contactpoint.model.ContactPointVersionCsvModel.Fields.openingHours;
import static ch.sbb.exportservice.job.prm.contactpoint.model.ContactPointVersionCsvModel.Fields.parentNumberServicePoint;
import static ch.sbb.exportservice.job.prm.contactpoint.model.ContactPointVersionCsvModel.Fields.parentSloidServicePoint;
import static ch.sbb.exportservice.job.prm.contactpoint.model.ContactPointVersionCsvModel.Fields.sloid;
import static ch.sbb.exportservice.job.prm.contactpoint.model.ContactPointVersionCsvModel.Fields.type;
import static ch.sbb.exportservice.job.prm.contactpoint.model.ContactPointVersionCsvModel.Fields.wheelchairAccess;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.job.BaseCsvWriter;
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
        sloid, parentSloidServicePoint, parentNumberServicePoint, type, designation, additionalInformation, inductionLoop,
        openingHours, wheelchairAccess, validFrom, validTo, creationDate, editionDate, status
    };
  }

}
