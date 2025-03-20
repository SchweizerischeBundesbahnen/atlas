package ch.sbb.exportservice.job.sepodi.loadingpoint;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.servicepoint.ReadLoadingPointVersionModel;
import ch.sbb.exportservice.job.BaseJsonWriter;
import org.springframework.stereotype.Component;

@Component
public class JsonLoadingPointVersionWriter extends BaseJsonWriter<ReadLoadingPointVersionModel> {

  JsonLoadingPointVersionWriter(FileService fileService) {
    super(fileService);
  }

}
