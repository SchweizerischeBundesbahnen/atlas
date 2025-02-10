package ch.sbb.importservice.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.importservice.entity.BulkImport;
import ch.sbb.importservice.model.BulkImportConfig;
import java.util.List;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class LogFileNotFoundException extends AtlasException {

  private final BulkImport bulkImport;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.NOT_FOUND.value())
        .message("Logfile for import with id " + bulkImport.getId() + " not found")
        .error("Logfile not found")
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message("Logfile for import with id " + bulkImport.getId() + " not found")
        .displayInfo(DisplayInfo.builder()
            .code("BULK_IMPORT.ERROR.LOGFILE_NOT_FOUND")
            .build())
        .build());
  }
}
