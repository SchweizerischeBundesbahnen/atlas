package ch.sbb.importservice.service.bulk.writer;

import ch.sbb.importservice.model.BulkImportConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@StepScope
public class BulkImportWriters {

  private static final Map<BulkImportConfig, BulkImportItemWriter> WRITERS = new HashMap<>();

  @Autowired
  public BulkImportWriters(List<BulkImportItemWriter> itemWriters) {
    itemWriters.forEach(writer -> WRITERS.put(writer.getBulkImportConfig(), writer));
  }

  public BulkImportItemWriter getWriter(BulkImportConfig bulkImportConfig) {
    if (!WRITERS.containsKey(bulkImportConfig)) {
      throw new UnsupportedOperationException("BulkImportConfig not supported yet" + bulkImportConfig);
    }
    return WRITERS.get(bulkImportConfig);
  }

}
