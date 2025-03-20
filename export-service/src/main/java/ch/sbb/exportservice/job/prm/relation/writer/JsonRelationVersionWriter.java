package ch.sbb.exportservice.job.prm.relation.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.prm.model.relation.ReadRelationVersionModel;
import ch.sbb.exportservice.job.BaseJsonWriter;
import org.springframework.stereotype.Component;

@Component
public class JsonRelationVersionWriter extends BaseJsonWriter<ReadRelationVersionModel> {

  JsonRelationVersionWriter(FileService fileService) {
    super(fileService);
  }

}
