package ch.sbb.exportservice.job.lidi.subline.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.lidi.ReadSublineVersionModelV2;
import ch.sbb.exportservice.job.BaseJsonWriter;
import org.springframework.stereotype.Component;

@Component
public class JsonSublineWriter extends BaseJsonWriter<ReadSublineVersionModelV2> {

  JsonSublineWriter(FileService fileService) {
    super(fileService);
  }

}
