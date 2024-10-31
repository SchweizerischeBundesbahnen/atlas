package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Service Points Bulk Import")
public interface ServicePointBulkImportApiV1 {

  String BASEPATH = "v1/service-points/bulk-import";

  @PostMapping(value = BASEPATH + "/update")
  List<BulkImportItemExecutionResult> bulkImportUpdate(@RequestBody List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> bulkImportContainers);
}
