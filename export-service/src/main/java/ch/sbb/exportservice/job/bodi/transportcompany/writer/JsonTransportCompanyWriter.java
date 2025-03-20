package ch.sbb.exportservice.job.bodi.transportcompany.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.exportservice.job.BaseJsonWriter;
import org.springframework.stereotype.Component;

@Component
public class JsonTransportCompanyWriter extends BaseJsonWriter<TransportCompanyModel> {

  JsonTransportCompanyWriter(FileService fileService) {
    super(fileService);
  }

}
