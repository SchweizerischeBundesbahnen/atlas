package ch.sbb.line.directory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.List;
import java.util.TreeSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class NotAPdfDocumentException extends AtlasException {

  private final List<String> documentFileNames;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("The files " + Strings.join(documentFileNames, ',') + " are not PDFs")
        .error("Not all files are valid PDFs")
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message("The files " + Strings.join(documentFileNames, ',') + " are not PDFs")
        .field("documents")
        .displayInfo(DisplayInfo.builder()
            .code("COMMON.FILEUPLOAD.ERROR.NOT_A_PDF")
            .with("documents", Strings.join(documentFileNames, ','))
            .build())
        .build());
  }
}
