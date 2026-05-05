package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.annotation.AuthorizedOnly;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.create.SectorCreateCsvModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Sector Bulk Import")
public interface SectorBulkImportApi {

  String BASEPATH = "internal/sectors/bulk-import";

  @AuthorizedOnly
  @PostMapping(value = BASEPATH + "/create")
  List<BulkImportItemExecutionResult> bulkImportCreate(
      @RequestBody List<BulkImportUpdateContainer<SectorCreateCsvModel>> bulkImportCreateContainers);

}
