package ch.sbb.exportservice.job.prm.parkinglot.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.prm.model.parkinglot.ReadParkingLotVersionModel;
import ch.sbb.exportservice.job.BaseJsonWriter;
import org.springframework.stereotype.Component;

@Component
public class JsonParkingLotVersionWriter extends BaseJsonWriter<ReadParkingLotVersionModel> {

  JsonParkingLotVersionWriter(FileService fileService) {
    super(fileService);
  }

}
