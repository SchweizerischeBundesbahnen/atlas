package ch.sbb.exportservice.job.lidi.line.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.lidi.LineVersionModelV2;
import ch.sbb.exportservice.job.BaseJsonWriter;
import org.springframework.stereotype.Component;

@Component
public class JsonLineWriter extends BaseJsonWriter<LineVersionModelV2> {

  JsonLineWriter(FileService fileService) {
    super(fileService);
  }

}
