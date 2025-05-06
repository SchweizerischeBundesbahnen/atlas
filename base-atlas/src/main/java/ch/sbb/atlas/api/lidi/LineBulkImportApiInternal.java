package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.LineUpdateCsvModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[INTERNAL] Line Bulk Import")
public interface LineBulkImportApiInternal {

  String BASEPATH = "internal/line/bulk-import";

  @PostMapping(value = BASEPATH + "/update")
  List<BulkImportItemExecutionResult> lineUpdate(
      @RequestBody List<BulkImportUpdateContainer<LineUpdateCsvModel>> bulkImportUpdateContainers);

}
