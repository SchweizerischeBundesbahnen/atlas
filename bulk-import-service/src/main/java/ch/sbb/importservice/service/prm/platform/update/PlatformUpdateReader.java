package ch.sbb.importservice.service.prm.platform.update;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.PlatformReducedUpdateCsvModel;
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
public class PlatformUpdateReader extends PlatformUpdate implements BulkImportItemReader {

  @Override
  public List<BulkImportUpdateContainer<?>> apply(File file) {
    List<BulkImportUpdateContainer<PlatformReducedUpdateCsvModel>> platformUpdateCsvModels = ReaderUtil.readAndValidate(file,
        PlatformReducedUpdateCsvModel.class);

    log.info("Read {} lines to import", platformUpdateCsvModels.size());
    return new ArrayList<>(platformUpdateCsvModels);
  }

  @Override
  public Class<?> getCsvModelClass() { return PlatformReducedUpdateCsvModel.class; }

}
