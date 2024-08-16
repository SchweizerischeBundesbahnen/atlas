package ch.sbb.importservice.service.sepodi.service.point.update;

import ch.sbb.atlas.imports.BulkImportContainer;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.importservice.client.SePoDiClient;
import ch.sbb.importservice.model.ImportType;
import ch.sbb.importservice.writer.BulkImportItemWriter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicePointUpdateWriter extends ServicePointUpdate implements BulkImportItemWriter {

  private final SePoDiClient sePoDiClient;

  @Override
  public void accept(Chunk<? extends BulkImportContainer> items) {
    List<BulkImportContainer> containers = new ArrayList<>(items.getItems());
    log.info("writing {} containers={}", containers.size(), containers);
    List<ItemImportResult> importResult = sePoDiClient.bulkImportServicePoints(ImportType.UPDATE, containers);

    // itemResult to log file
  }
}
