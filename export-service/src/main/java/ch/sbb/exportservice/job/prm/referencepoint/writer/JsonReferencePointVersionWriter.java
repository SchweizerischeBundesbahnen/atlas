package ch.sbb.exportservice.job.prm.referencepoint.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.prm.model.referencepoint.ReadReferencePointVersionModel;
import ch.sbb.exportservice.job.BaseJsonWriter;
import org.springframework.stereotype.Component;

@Component
public class JsonReferencePointVersionWriter extends BaseJsonWriter<ReadReferencePointVersionModel> {

  JsonReferencePointVersionWriter(FileService fileService) {
    super(fileService);
  }

}
