package ch.sbb.atlas.api.prm;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.PlatformUpdateCsvModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Platform Bulk Import")
public interface PlatformBulkImportApiV1 {

  String BASEPATH = "v1/platform/bulk-import";

  @PostMapping(value = BASEPATH + "/update-platform")
  List<BulkImportItemExecutionResult> bulkImportPlatformUpdate(
      @RequestBody List<BulkImportUpdateContainer<PlatformUpdateCsvModel>> bulkImportUpdateContainers);

}
