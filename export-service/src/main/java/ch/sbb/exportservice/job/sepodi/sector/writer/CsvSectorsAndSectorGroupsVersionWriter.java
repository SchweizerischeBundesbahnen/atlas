package ch.sbb.exportservice.job.sepodi.sector.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.job.BaseCsvWriter;
import ch.sbb.exportservice.job.sepodi.sector.model.SectorAndSectorGroupCsvModel;
import ch.sbb.exportservice.job.sepodi.sector.model.SectorAndSectorGroupCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvSectorsAndSectorGroupsVersionWriter extends BaseCsvWriter<SectorAndSectorGroupCsvModel> {

  CsvSectorsAndSectorGroupsVersionWriter(FileService fileService) {
    super(fileService);
  }

  @Override
  protected String[] getCsvHeader() {
    return new String[]{
        Fields.sloid, Fields.type, Fields.trafficPointSloid, Fields.validFrom, Fields.validTo,
        Fields.designation, Fields.length, Fields.edgeHeight,
        Fields.lv95East, Fields.lv95North,
        Fields.wgs84East, Fields.wgs84North,
        Fields.height, Fields.spatialReference,
        Fields.relatedGroups, Fields.relatedSectors, Fields.status,
        Fields.creationDate, Fields.editionDate
    };
  }

}
