package ch.sbb.exportservice.job.sepodi.sector.writer;

import ch.sbb.atlas.s3.service.FileService;
import ch.sbb.atlas.api.servicepoint.sector.SectorVersionModel;
import ch.sbb.exportservice.job.BaseJsonWriter;
import org.springframework.stereotype.Component;

@Component
public class JsonSectorVersionWriter extends BaseJsonWriter<SectorVersionModel> {

  public JsonSectorVersionWriter(FileService fileService) {
    super(fileService);
  }
}
