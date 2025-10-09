package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.imports.BulkImportItemExecutionResult;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.TrafficPointUpdateCsvModel;
import ch.sbb.atlas.imports.model.create.TrafficPointCreateCsvModel;
import ch.sbb.atlas.imports.model.terminate.TrafficPointTerminateCsvModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Traffic Point Element Bulk Import")
public interface TrafficPointBulkImportApi {

  String BASEPATH = "internal/traffic-points/bulk-import";

  @PostMapping(value = BASEPATH + "/create")
  List<BulkImportItemExecutionResult> bulkImportCreate(
      @RequestBody List<BulkImportUpdateContainer<TrafficPointCreateCsvModel>> bulkImportCreateContainers);

  @PostMapping(value = BASEPATH + "/update")
  List<BulkImportItemExecutionResult> bulkImportUpdate(
      @RequestBody List<BulkImportUpdateContainer<TrafficPointUpdateCsvModel>> bulkImportUpdateContainers);

  @PostMapping(value = BASEPATH + "/terminate")
  List<BulkImportItemExecutionResult> bulkImportTerminate(
      @RequestBody List<BulkImportUpdateContainer<TrafficPointTerminateCsvModel>> bulkImportUpdateContainers);

}
