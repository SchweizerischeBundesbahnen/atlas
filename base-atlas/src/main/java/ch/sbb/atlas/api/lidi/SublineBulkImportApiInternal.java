package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.SublineUpdateCsvModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Subline Bulk Import")
public interface SublineBulkImportApiInternal {

  String BASEPATH = "internal/subline/bulk-import";

  @PreAuthorize("""
      @bulkImportUserAdministrationService.hasPermissionsForBulkImport(T(ch.sbb.atlas.imports.bulk.model.ImportType).UPDATE,
      T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).LIDI)""")
  @PostMapping(value = BASEPATH + "/update")
  List<BulkImportItemExecutionResult> sublineUpdate(
      @RequestBody List<BulkImportUpdateContainer<SublineUpdateCsvModel>> bulkImportUpdateContainers);

}

