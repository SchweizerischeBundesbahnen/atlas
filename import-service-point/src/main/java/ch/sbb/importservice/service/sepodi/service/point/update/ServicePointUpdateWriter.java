package ch.sbb.importservice.service.sepodi.service.point.update;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.bulk.BulkImportContainer;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.importservice.client.ServicePointBulkImportClient;
import ch.sbb.importservice.service.bulk.writer.BulkImportItemWriter;
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

  private final ServicePointBulkImportClient servicePointBulkImportClient;

  @Override
  public void accept(Chunk<? extends BulkImportContainer> items) {
    List<BulkImportContainer> containers = new ArrayList<>(items.getItems());

    List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> updateContainers =
        containers.stream()
            .map(i -> (BulkImportUpdateContainer<ServicePointUpdateCsvModel>)i)
            .toList();

    log.info("Writing {} containers to service-point-directory", updateContainers.size());
    List<ItemImportResult> importResult = servicePointBulkImportClient.bulkImportUpdate(updateContainers);

    // itemResult to log file
    log.info("Import result: {}", importResult);
  }
}
