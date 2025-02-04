package ch.sbb.atlas.model.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class FileNotFoundOnS3Exception extends AtlasException {

  private final String filePath;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .message("""
            File not found on S3. The export might not have run yet.
            Please consult the docs at: https://export-service.prod.app.sbb.ch/static/rest-api.html for data availability.
            """)
        .error("File " + filePath + " not found on atlas amazon s3 bucket.")
        .details(new TreeSet<>())
        .build();
  }

}
