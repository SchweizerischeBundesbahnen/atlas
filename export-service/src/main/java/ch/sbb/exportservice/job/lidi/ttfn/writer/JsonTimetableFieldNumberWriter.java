package ch.sbb.exportservice.job.lidi.ttfn.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberVersionModel;
import ch.sbb.exportservice.job.BaseJsonWriter;
import org.springframework.stereotype.Component;

@Component
public class JsonTimetableFieldNumberWriter extends BaseJsonWriter<TimetableFieldNumberVersionModel> {

  JsonTimetableFieldNumberWriter(FileService fileService) {
    super(fileService);
  }

}
