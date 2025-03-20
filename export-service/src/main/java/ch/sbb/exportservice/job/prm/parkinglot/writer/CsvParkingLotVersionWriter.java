package ch.sbb.exportservice.job.prm.parkinglot.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.job.BaseCsvWriter;
import ch.sbb.exportservice.job.prm.BasePrmCsvModel;
import ch.sbb.exportservice.job.prm.parkinglot.model.ParkingLotVersionCsvModel;
import ch.sbb.exportservice.job.prm.parkinglot.model.ParkingLotVersionCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvParkingLotVersionWriter extends BaseCsvWriter<ParkingLotVersionCsvModel> {

  CsvParkingLotVersionWriter(FileService fileService) {
    super(fileService);
  }

  @Override
  protected String[] getCsvHeader() {
    return new String[]{
        Fields.sloid, Fields.parentSloidServicePoint, Fields.parentNumberServicePoint, Fields.designation,
        Fields.additionalInformation, Fields.placesAvailable, Fields.prmPlacesAvailable,
        BasePrmCsvModel.Fields.validFrom, BasePrmCsvModel.Fields.validTo, BasePrmCsvModel.Fields.creationDate,
        BasePrmCsvModel.Fields.editionDate, BasePrmCsvModel.Fields.status
    };
  }

}
