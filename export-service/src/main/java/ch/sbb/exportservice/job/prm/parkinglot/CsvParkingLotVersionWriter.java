package ch.sbb.exportservice.job.prm.parkinglot;

import ch.sbb.atlas.export.model.prm.BasePrmCsvModel;
import ch.sbb.atlas.export.model.prm.ParkingLotVersionCsvModel;
import ch.sbb.atlas.export.model.prm.ParkingLotVersionCsvModel.Fields;
import ch.sbb.exportservice.job.BaseCsvWriter;
import org.springframework.stereotype.Component;

@Component
public class CsvParkingLotVersionWriter extends BaseCsvWriter<ParkingLotVersionCsvModel> {

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
