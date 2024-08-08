package ch.sbb.importservice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class BulkImportControllerTest extends BaseControllerApiTest {

  @Test
  void shouldAcceptGenericBulkImportWithFile() throws Exception {
    mvc.perform(multipart("/v1/import/bulk/SEPODI/SERVICE_POINT/UPDATE")
            .file("file", "example".getBytes(StandardCharsets.UTF_8))
            .contentType(contentType))
        .andExpect(status().isAccepted());
  }
}