package ch.sbb.importservice.module.bulkimport.job.sepodi.trafficpoint.terminate;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.terminate.TrafficPointTerminateCsvModel;
import ch.sbb.importservice.module.bulkimport.reader.BulkImportItemReader;
import ch.sbb.importservice.module.bulkimport.reader.ReaderUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrafficPointTerminateReader extends TrafficPointTerminate implements BulkImportItemReader {

  @Override
  public List<BulkImportUpdateContainer<?>> apply(File file) {
    List<BulkImportUpdateContainer<TrafficPointTerminateCsvModel>> trafficPointTerminateCsvModels = ReaderUtil.readAndValidate(file,
        TrafficPointTerminateCsvModel.class);

    log.info("Read {} lines to import", trafficPointTerminateCsvModels.size());
    return new ArrayList<>(trafficPointTerminateCsvModels);
  }

  @Override
  public Class<?> getCsvModelClass() {
    return TrafficPointTerminateCsvModel.class;
  }

}
