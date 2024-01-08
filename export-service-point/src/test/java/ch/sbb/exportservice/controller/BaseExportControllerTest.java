package ch.sbb.exportservice.controller;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import java.io.InputStream;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public abstract class BaseExportControllerTest extends BaseControllerApiTest {
  protected static final String JSON_DATA = """
        [
        {"number": 1205887},
        {"number": 1205888},
        {"number": 1205889}
      ]
      """;

  protected StreamingResponseBody writeOutputStream(InputStream inputStream) {
    return outputStream -> {
      inputStream.transferTo(outputStream);
      inputStream.close();
    };
  }
}
