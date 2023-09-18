package ch.sbb.line.directory.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.service.hearing.TikaService;
import java.io.File;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
 class TikaServiceTest {

  private final TikaService tikaService;

  @Autowired
   TikaServiceTest(TikaService tikaService) {
    this.tikaService = tikaService;
  }

  @Test
   void givenPdfFile_whenIsFilePdf_thenReturnTrue() {
    File file = new File("src/test/resources/pdf/dummy.pdf");

    assertTrue(tikaService.isFilePdf(file));
  }

  @Test
   void givenTextFile_whenIsFilePdf_thenReturnFalse() {
    File file = new File("src/test/resources/pdf/test.txt");

    assertFalse(tikaService.isFilePdf(file));
  }

  @Test
   void givenNoFile_whenIsFilePdf_thenException() {
    File file = new File("src/test/resources/pdf/nonexistingfile.txt");

    assertThrows(IllegalStateException.class, () -> tikaService.isFilePdf(file));
  }

}
