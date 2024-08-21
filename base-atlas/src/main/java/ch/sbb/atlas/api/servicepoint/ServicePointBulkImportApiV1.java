package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Service Points")
public interface ServicePointBulkImportApiV1 {

  String BASEPATH = "v1/service-points/bulk-import";

  @PostMapping(value = BASEPATH + "/update")
  List<ItemImportResult> bulkImportUpdate(@Valid @RequestBody List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> bulkImportContainers);
}
