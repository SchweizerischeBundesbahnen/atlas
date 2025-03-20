package ch.sbb.exportservice.job.prm.toilet.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.job.BaseCsvWriter;
import ch.sbb.exportservice.job.prm.BasePrmCsvModel;
import ch.sbb.exportservice.job.prm.toilet.model.ToiletVersionCsvModel;
import ch.sbb.exportservice.job.prm.toilet.model.ToiletVersionCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvToiletVersionWriter extends BaseCsvWriter<ToiletVersionCsvModel> {

  CsvToiletVersionWriter(FileService fileService) {
    super(fileService);
  }

  @Override
  protected String[] getCsvHeader() {
    return new String[]{
        Fields.sloid, Fields.parentSloidServicePoint, Fields.parentNumberServicePoint, Fields.designation,
        Fields.additionalInformation, Fields.wheelchairToilet,
        BasePrmCsvModel.Fields.validFrom, BasePrmCsvModel.Fields.validTo, BasePrmCsvModel.Fields.creationDate,
        BasePrmCsvModel.Fields.editionDate, BasePrmCsvModel.Fields.status
    };
  }

}
