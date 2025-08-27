package ch.sbb.importservice.module.bulkimport.reader;

import ch.sbb.importservice.module.bulkimport.exception.BulkImportNotImplementedException;
import ch.sbb.importservice.module.bulkimport.model.BulkImportConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BulkImportReaders {

  private static final Map<BulkImportConfig, BulkImportItemReader> READERS = new HashMap<>();

  @Autowired
  public BulkImportReaders(List<BulkImportItemReader> itemReaders) {
    itemReaders.forEach(reader -> READERS.put(reader.getBulkImportConfig(), reader));
  }

  public BulkImportItemReader getReaderFunction(BulkImportConfig bulkImportConfig) {
    if (!READERS.containsKey(bulkImportConfig)) {
      throw new BulkImportNotImplementedException(bulkImportConfig);
    }
    return READERS.get(bulkImportConfig);
  }

}
