package ch.sbb.exportservice.writer;

import ch.sbb.atlas.export.model.prm.ParkingLotVersionCsvModel;
import ch.sbb.atlas.export.model.prm.ParkingLotVersionCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvParkingLotVersionWriter extends BaseCsvWriter<ParkingLotVersionCsvModel> {

  @Override
  String[] getCsvHeader() {
    return new String[]{
        Fields.sloid, Fields.parentSloidServicePoint, Fields.parentNumberServicePoint, Fields.designation,
        Fields.additionalInformation, Fields.placesAvailable, Fields.prmPlacesAvailable,
        Fields.validFrom, Fields.validTo, Fields.creationDate, Fields.editionDate
    };
  }

}
