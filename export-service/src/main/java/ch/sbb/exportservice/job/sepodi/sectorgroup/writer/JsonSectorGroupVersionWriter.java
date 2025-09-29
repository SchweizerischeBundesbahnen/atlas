package ch.sbb.exportservice.job.sepodi.sectorgroup.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.servicepoint.sector.SectorGroupVersionModel;
import ch.sbb.exportservice.job.BaseJsonWriter;
import org.springframework.stereotype.Component;

@Component
public class JsonSectorGroupVersionWriter extends BaseJsonWriter<SectorGroupVersionModel> {

  public JsonSectorGroupVersionWriter(FileService fileService) {
    super(fileService);
  }
}
