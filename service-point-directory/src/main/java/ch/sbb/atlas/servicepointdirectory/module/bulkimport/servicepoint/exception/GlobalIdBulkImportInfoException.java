package ch.sbb.atlas.servicepointdirectory.module.bulkimport.servicepoint.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import java.util.List;
import java.util.TreeSet;
import lombok.Getter;

@Getter
public final class GlobalIdBulkImportInfoException extends AtlasException {

  private static final String FIELD = "globalId";
  private static final String CODE_PREFIX = "SEPODI.SERVICE_POINTS.GLOBAL_ID_INFO.";

  private final String message;
  private final String code;
  private final transient DisplayInfo displayInfo;

  private GlobalIdBulkImportInfoException(String message, String code, DisplayInfo displayInfo) {
    this.message = message;
    this.code = code;
    this.displayInfo = displayInfo;
  }

  public static GlobalIdBulkImportInfoException repointed(String globalId, ServicePointNumber displacedServicePoint) {
    String displaced = String.valueOf(displacedServicePoint.getNumber());
    return new GlobalIdBulkImportInfoException(
        "Global-ID " + globalId + " was taken from service point " + displaced + ".",
        CODE_PREFIX + "REPOINTED",
        DisplayInfo.builder()
            .code(CODE_PREFIX + "REPOINTED")
            .with(FIELD, globalId)
            .with("servicePointNumber", displaced)
            .build());
  }

  public static GlobalIdBulkImportInfoException removed(String globalId) {
    return new GlobalIdBulkImportInfoException(
        "Global-ID " + globalId + " was removed.",
        CODE_PREFIX + "REMOVED",
        DisplayInfo.builder()
            .code(CODE_PREFIX + "REMOVED")
            .with(FIELD, globalId)
            .build());
  }

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(ErrorResponse.VERSIONING_NO_CHANGES_HTTP_STATUS)
        .error("Global-ID bulk import info")
        .message(message)
        .details(new TreeSet<>(List.of(Detail.builder()
            .field(FIELD)
            .message(message)
            .displayInfo(displayInfo)
            .build())))
        .build();
  }
}
