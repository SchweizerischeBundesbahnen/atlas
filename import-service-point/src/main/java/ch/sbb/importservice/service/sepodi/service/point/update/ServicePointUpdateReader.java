package ch.sbb.importservice.service.sepodi.service.point.update;

import ch.sbb.atlas.imports.bulk.AtlasCsvReader;
import ch.sbb.atlas.imports.bulk.BulkImportContainer;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.importservice.service.BulkImportItemReader;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ServicePointUpdateReader extends ServicePointUpdate implements BulkImportItemReader {

  @Override
  public List<BulkImportContainer> apply(File file) {
    //How to make sure template corresponds with this reader?
    List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> servicePointUpdateCsvModels = AtlasCsvReader.readLinesFromFileWithNullingValue(file, ServicePointUpdateCsvModel.class);

    log.info("Read {} lines", servicePointUpdateCsvModels.size());
    return servicePointUpdateCsvModels.stream().collect(Collectors.toUnmodifiableList());
  }


}
