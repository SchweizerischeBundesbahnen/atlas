package ch.sbb.atlas.api.prm;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.PlatformCompleteUpdateCsvModel;
import ch.sbb.atlas.imports.model.PlatformReducedUpdateCsvModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Platform Bulk Import")
public interface PlatformBulkImportApiV1 {

  String BASEPATH = "internal/platform/bulk-import";

  @PostMapping(value = BASEPATH + "/update-platform-reduced")
  List<BulkImportItemExecutionResult> bulkImportPlatformReducedUpdate(
      @RequestBody List<BulkImportUpdateContainer<PlatformReducedUpdateCsvModel>> bulkImportUpdateContainers);

  @PostMapping(value = BASEPATH + "/update-platform-complete")
  List<BulkImportItemExecutionResult> bulkImportPlatformCompleteUpdate(
      @RequestBody List<BulkImportUpdateContainer<PlatformCompleteUpdateCsvModel>> bulkImportUpdateContainers);

}
