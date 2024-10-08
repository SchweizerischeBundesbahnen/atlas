package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.TrafficPointUpdateCsvModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Traffic Points Bulk Import")
public interface TrafficPointBulkImportApiV1 {

  String BASEPATH = "v1/traffic-points/bulk-import";

  @PostMapping(value = BASEPATH + "/update")
  @PreAuthorize("@bulkImportUserAdministrationService.hasPermissionsForBulkImport(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).SEPODI)")
  List<BulkImportItemExecutionResult> bulkImportUpdate(@RequestBody List<BulkImportUpdateContainer<TrafficPointUpdateCsvModel>> bulkImportUpdateContainers);

}
