package ch.sbb.atlas.api.prm;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.terminate.SloidTerminateCsvModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Stop Point Bulk Import")
public interface StopPointBulkImportApi {

  String BASEPATH = "internal/stop-point/bulk-import";

  @PostMapping(value = BASEPATH + "/terminate-stop-point")
  @PreAuthorize("""
      @bulkImportUserAdministrationService.hasPermissionsForBulkImport(T(ch.sbb.atlas.imports.bulk.model.ImportType).TERMINATE,
      T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).PRM)""")
  List<BulkImportItemExecutionResult> bulkImportStopPointTerminate(
      @RequestBody List<BulkImportUpdateContainer<SloidTerminateCsvModel>> bulkImportUpdateContainers);
}
