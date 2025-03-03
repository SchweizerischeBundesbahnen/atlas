package ch.sbb.importservice.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.importservice.model.BulkImportConfig;
import java.util.List;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class BulkImportNotImplementedException extends AtlasException {

  private final BulkImportConfig bulkImportConfig;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.NOT_IMPLEMENTED.value())
        .message("BulkImport Scenario " + bulkImportConfig + " not implemented yet")
        .error("BulkImport Scenario not implemented yet")
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message("BulkImport Scenario " + bulkImportConfig + " not implemented yet")
        .displayInfo(DisplayInfo.builder()
            .code("BULK_IMPORT.ERROR.SCENARIO_NOT_IMPLEMENTED")
            .build())
        .build());
  }
}
