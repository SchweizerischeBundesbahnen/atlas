package ch.sbb.atlas.api.prm;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.PlatformCompleteUpdateCsvModel;
import ch.sbb.atlas.imports.model.PlatformReducedUpdateCsvModel;
import ch.sbb.atlas.imports.model.terminate.SloidTerminateCsvModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Platform Bulk Import")
public interface PlatformBulkImportApi {

  String BASEPATH = "internal/platform/bulk-import";

  @PreAuthorize("""
      @bulkImportUserAdministrationService.hasPermissionsForBulkImport(T(ch.sbb.atlas.imports.bulk.model.ImportType).UPDATE,
      T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).PRM)""")
  @PostMapping(value = BASEPATH + "/update-platform-reduced")
  List<BulkImportItemExecutionResult> bulkImportPlatformReducedUpdate(
      @RequestBody List<BulkImportUpdateContainer<PlatformReducedUpdateCsvModel>> bulkImportUpdateContainers);

  @PreAuthorize("""
      @bulkImportUserAdministrationService.hasPermissionsForBulkImport(T(ch.sbb.atlas.imports.bulk.model.ImportType).UPDATE,
      T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).PRM)""")
  @PostMapping(value = BASEPATH + "/update-platform-complete")
  List<BulkImportItemExecutionResult> bulkImportPlatformCompleteUpdate(
      @RequestBody List<BulkImportUpdateContainer<PlatformCompleteUpdateCsvModel>> bulkImportUpdateContainers);

  @PostMapping(value = BASEPATH + "/terminate-platform")
  @PreAuthorize("""
      @bulkImportUserAdministrationService.hasPermissionsForBulkImport(T(ch.sbb.atlas.imports.bulk.model.ImportType).TERMINATE,
      T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).PRM)""")
  List<BulkImportItemExecutionResult> bulkImportPlatformTerminate(
      @RequestBody List<BulkImportUpdateContainer<SloidTerminateCsvModel>> bulkImportUpdateContainers);
}
