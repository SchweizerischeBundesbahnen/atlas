package ch.sbb.importservice.service.sepodi.traffic.point.create;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.create.TrafficPointCreateCsvModel;
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
public class TrafficPointCreateReader extends TrafficPointCreate implements BulkImportItemReader {

  @Override
  public List<BulkImportUpdateContainer<?>> apply(File file) {
    List<BulkImportUpdateContainer<TrafficPointCreateCsvModel>> trafficPointcreateCsvModels = ReaderUtil.readAndValidate(file,
        TrafficPointCreateCsvModel.class);

    log.info("Read {} lines to import", trafficPointcreateCsvModels.size());
    return new ArrayList<>(trafficPointcreateCsvModels);
  }

  @Override
  public Class<?> getCsvModelClass() {
    return TrafficPointCreateCsvModel.class;
  }

}
