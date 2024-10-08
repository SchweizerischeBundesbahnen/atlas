package ch.sbb.importservice.service.sepodi.traffic.point.update;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.TrafficPointUpdateCsvModel;
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
public class TrafficPointUpdateReader extends TrafficPointUpdate implements BulkImportItemReader {

  @Override
  public List<BulkImportUpdateContainer<?>> apply(File file) {
    List<BulkImportUpdateContainer<TrafficPointUpdateCsvModel>> trafficPointUpdateCsvModels = ReaderUtil.readAndValidate(file,
        TrafficPointUpdateCsvModel.class);

    log.info("Read {} lines to import", trafficPointUpdateCsvModels.size());
    return new ArrayList<>(trafficPointUpdateCsvModels);
  }

  @Override
  public Class<?> getCsvModelClass() {
    return TrafficPointUpdateCsvModel.class;
  }

}
