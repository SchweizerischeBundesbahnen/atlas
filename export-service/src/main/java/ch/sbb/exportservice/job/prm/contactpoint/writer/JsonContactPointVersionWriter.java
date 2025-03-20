package ch.sbb.exportservice.job.prm.contactpoint.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.prm.model.contactpoint.ReadContactPointVersionModel;
import ch.sbb.exportservice.job.BaseJsonWriter;
import org.springframework.stereotype.Component;

@Component
public class JsonContactPointVersionWriter extends BaseJsonWriter<ReadContactPointVersionModel> {

  JsonContactPointVersionWriter(FileService fileService) {
    super(fileService);
  }

}
