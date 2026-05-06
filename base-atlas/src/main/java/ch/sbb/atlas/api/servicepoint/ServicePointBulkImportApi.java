package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.model.create.ServicePointCreateCsvModel;
import ch.sbb.atlas.imports.model.terminate.ServicePointTerminateCsvModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Service Point Bulk Import")
public interface ServicePointBulkImportApi {

  String BASEPATH = "internal/service-points/bulk-import";

  @PreAuthorize("""
      @bulkImportUserAdministrationService.hasPermissionsForBulkImport(T(ch.sbb.atlas.imports.bulk.model.ImportType).UPDATE,
      T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)""")
  @PostMapping(value = BASEPATH + "/update")
  List<BulkImportItemExecutionResult> bulkImportUpdate(
      @RequestBody List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> bulkImportContainers);

  @PreAuthorize("""
      @bulkImportUserAdministrationService.hasPermissionsForBulkImport(T(ch.sbb.atlas.imports.bulk.model.ImportType).CREATE,
      T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)""")
  @PostMapping(value = BASEPATH + "/create")
  List<BulkImportItemExecutionResult> bulkImportCreate(
      @RequestBody List<BulkImportUpdateContainer<ServicePointCreateCsvModel>> bulkImportContainers);

  @PreAuthorize("""
      @bulkImportUserAdministrationService.hasPermissionsForBulkImport(T(ch.sbb.atlas.imports.bulk.model.ImportType).TERMINATE,
      T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).SEPODI)""")
  @PostMapping(value = BASEPATH + "/terminate")
  List<BulkImportItemExecutionResult> bulkImportTerminate(
      @RequestBody List<BulkImportUpdateContainer<ServicePointTerminateCsvModel>> bulkImportContainers);

}
