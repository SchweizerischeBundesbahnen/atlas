package ch.sbb.exportservice.job.prm.stoppoint.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.prm.model.stoppoint.ReadStopPointVersionModel;
import ch.sbb.exportservice.job.BaseJsonWriter;
import org.springframework.stereotype.Component;

@Component
public class JsonStopPointVersionWriter extends BaseJsonWriter<ReadStopPointVersionModel> {

  JsonStopPointVersionWriter(FileService fileService) {
    super(fileService);
  }

}
