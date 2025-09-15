package ch.sbb.importservice.module.bulkimport.job.prm.platform.update.complete;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.PlatformCompleteUpdateCsvModel;
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
public class PlatformCompleteUpdateReader extends PlatformCompleteUpdate implements BulkImportItemReader {

  @Override
  public List<BulkImportUpdateContainer<?>> apply(File file) {
    List<BulkImportUpdateContainer<PlatformCompleteUpdateCsvModel>> platformUpdateCsvModels =
        ReaderUtil.readAndValidate(file,
            PlatformCompleteUpdateCsvModel.class);

    log.info("Read {} lines to import", platformUpdateCsvModels.size());
    return new ArrayList<>(platformUpdateCsvModels);
  }

  @Override
  public Class<?> getCsvModelClass() {
    return PlatformCompleteUpdateCsvModel.class;
  }

}
