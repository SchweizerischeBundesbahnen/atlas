package ch.sbb.exportservice.job.prm.toilet.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.prm.model.toilet.ReadToiletVersionModel;
import ch.sbb.exportservice.job.BaseJsonWriter;
import org.springframework.stereotype.Component;

@Component
public class JsonToiletVersionWriter extends BaseJsonWriter<ReadToiletVersionModel> {

  JsonToiletVersionWriter(FileService fileService) {
    super(fileService);
  }

}
