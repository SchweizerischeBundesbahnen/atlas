package ch.sbb.importservice.service.sepodi.service.point.terminate;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.model.terminate.ServicePointTerminateCsvModel;
import ch.sbb.importservice.service.bulk.reader.BulkImportItemReader;
import ch.sbb.importservice.service.bulk.reader.ReaderUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicePointTerminateReader extends ServicePointTerminate implements BulkImportItemReader {

  @Override
  public List<BulkImportUpdateContainer<?>> apply(File file) {
    List<BulkImportUpdateContainer<ServicePointTerminateCsvModel>> servicePointUpdateCsvModels = ReaderUtil.readAndValidate(file,
        ServicePointTerminateCsvModel.class);

    log.info("Read {} lines to import", servicePointUpdateCsvModels.size());
    return new ArrayList<>(servicePointUpdateCsvModels);
  }

  @Override
  public Class<?> getCsvModelClass() {
    return ServicePointUpdateCsvModel.class;
  }

}
