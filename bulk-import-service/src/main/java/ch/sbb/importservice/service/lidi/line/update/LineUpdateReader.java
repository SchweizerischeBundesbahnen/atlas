package ch.sbb.importservice.service.lidi.line.update;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.LineUpdateCsvModel;
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
public class LineUpdateReader extends LineUpdate implements BulkImportItemReader {

  @Override
  public List<BulkImportUpdateContainer<?>> apply(File file) {
    List<BulkImportUpdateContainer<LineUpdateCsvModel>> lineUpdateCsvModels = ReaderUtil.readAndValidate(file,
        LineUpdateCsvModel.class);

    log.info("Read {} lines to import", lineUpdateCsvModels.size());
    return new ArrayList<>(lineUpdateCsvModels);
  }

  @Override
  public Class<?> getCsvModelClass() {
    return LineUpdateCsvModel.class;
  }

}
