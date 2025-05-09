package ch.sbb.importservice.service.sepodi.service.point.terminate;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
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
    List<BulkImportUpdateContainer<ServicePointTerminateCsvModel>> servicePointTerminateCsvModel =
        ReaderUtil.readAndValidate(file,
            ServicePointTerminateCsvModel.class);

    log.info("Read {} lines to import", servicePointTerminateCsvModel.size());
    return new ArrayList<>(servicePointTerminateCsvModel);
  }

  @Override
  public Class<?> getCsvModelClass() {
    return ServicePointTerminateCsvModel.class;
  }

}
