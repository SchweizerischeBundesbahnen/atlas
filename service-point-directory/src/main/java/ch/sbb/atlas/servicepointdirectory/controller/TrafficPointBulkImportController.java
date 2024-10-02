package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.servicepoint.TrafficPointBulkImportApiV1;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.TrafficPointUpdateCsvModel;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TrafficPointBulkImportController implements TrafficPointBulkImportApiV1 {

  @Override
  public List<BulkImportItemExecutionResult> bulkImportUpdate(List<BulkImportUpdateContainer<TrafficPointUpdateCsvModel>> bulkImportUpdateContainers) {
    List<BulkImportItemExecutionResult> results = new ArrayList<>();
    log.info("bulkImportUpdateContainers size is: " + bulkImportUpdateContainers.size());
    return results;
  }

}
