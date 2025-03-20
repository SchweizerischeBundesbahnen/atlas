package ch.sbb.exportservice.job.sepodi.servicepoint.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.exportservice.job.BaseJsonWriter;
import org.springframework.stereotype.Component;

@Component
public class JsonServicePointVersionWriter extends BaseJsonWriter<ReadServicePointVersionModel> {

  JsonServicePointVersionWriter(FileService fileService) {
    super(fileService);
  }

}
