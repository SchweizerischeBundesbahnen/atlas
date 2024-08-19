package ch.sbb.importservice.service;

import ch.sbb.atlas.imports.bulk.BulkImportContainer;
import ch.sbb.importservice.model.BulkImportConfig;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BulkImportReaders {

  private static final Map<BulkImportConfig, BulkImportItemReader> READERS = new HashMap<>();

  @Autowired
  public BulkImportReaders(List<BulkImportItemReader> itemReaders) {
    itemReaders.forEach(reader -> READERS.put(reader.getBulkImportConfig(), reader));
  }

  public Function<File, List<BulkImportContainer>> getReaderFunction(BulkImportConfig bulkImportConfig) {
    if (!READERS.containsKey(bulkImportConfig)) {
      throw new UnsupportedOperationException("BulkImportConfig not supported yet" + bulkImportConfig);
    }
    return READERS.get(bulkImportConfig);
  }

}
