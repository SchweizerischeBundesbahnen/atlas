package ch.sbb.importservice.module.bulkimport.job.lidi.line.create;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.LineCreateCsvModel;
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
public class LineCreateReader extends LineCreate implements BulkImportItemReader {

  @Override
  public List<BulkImportUpdateContainer<?>> apply(File file) {
    List<BulkImportUpdateContainer<LineCreateCsvModel>> lineCreateCsvModels = ReaderUtil.readAndValidate(file,
        LineCreateCsvModel.class);

    log.info("Read {} lines to import", lineCreateCsvModels.size());
    return new ArrayList<>(lineCreateCsvModels);
  }

  @Override
  public Class<?> getCsvModelClass() {
    return LineCreateCsvModel.class;
  }

}
