package ch.sbb.importservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.importservice.repository.BulkImportRepository;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BulkImportControllerTest extends BaseControllerApiTest {

  @Autowired
  private BulkImportRepository bulkImportRepository;

  @AfterEach
  void tearDown() {
    bulkImportRepository.deleteAll();
  }

  @Test
  void shouldAcceptGenericBulkImportWithFile() throws Exception {
    mvc.perform(multipart("/v1/import/bulk/SEPODI/SERVICE_POINT/UPDATE")
            .file("file", "example".getBytes(StandardCharsets.UTF_8))
            .contentType(contentType))
        .andDo(print())
        .andExpect(status().isAccepted());

    assertThat(bulkImportRepository.count()).isEqualTo(1);
  }
}