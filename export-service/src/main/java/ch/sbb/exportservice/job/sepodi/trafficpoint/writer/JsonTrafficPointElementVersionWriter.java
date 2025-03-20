package ch.sbb.exportservice.job.sepodi.trafficpoint.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.servicepoint.ReadTrafficPointElementVersionModel;
import ch.sbb.exportservice.job.BaseJsonWriter;
import org.springframework.stereotype.Component;

@Component
public class JsonTrafficPointElementVersionWriter extends BaseJsonWriter<ReadTrafficPointElementVersionModel> {

  JsonTrafficPointElementVersionWriter(FileService fileService) {
    super(fileService);
  }

}
