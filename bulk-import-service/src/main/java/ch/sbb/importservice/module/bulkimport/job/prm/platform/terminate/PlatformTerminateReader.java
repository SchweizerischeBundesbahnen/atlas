package ch.sbb.importservice.module.bulkimport.job.prm.platform.terminate;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.terminate.SloidTerminateCsvModel;
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
public class PlatformTerminateReader extends PlatformTerminate implements BulkImportItemReader {

  @Override
  public List<BulkImportUpdateContainer<?>> apply(File file) {
    List<BulkImportUpdateContainer<SloidTerminateCsvModel>> platformTerminateCsvModels =
        ReaderUtil.readAndValidate(file,
            SloidTerminateCsvModel.class);

    log.info("Read {} lines to import", platformTerminateCsvModels.size());
    return new ArrayList<>(platformTerminateCsvModels);
  }

  @Override
  public Class<?> getCsvModelClass() {
    return SloidTerminateCsvModel.class;
  }

}
