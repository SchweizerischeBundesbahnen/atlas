package ch.sbb.importservice.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.List;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class ImporterMailNotFoundException extends AtlasException {

  private final UserModel importer;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.NOT_FOUND.value())
        .message("Mail not given for importer " + importer.getSbbUserId())
        .error("Mail not given in AzureAD")
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message("Mail not given for importer " + importer.getSbbUserId())
        .displayInfo(DisplayInfo.builder()
            .code("BULK_IMPORT.ERROR.IMPORTER_MAIL_NOT_FOUND")
            .build())
        .build());
  }
}
