package ch.sbb.line.directory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.service.hearing.TikaService;
import java.io.File;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class TikaServiceTest {

    private final TikaService tikaService;

    @Autowired
    public TikaServiceTest(TikaService tikaService) {
        this.tikaService = tikaService;
    }

    @Test
    public void givenPdfFile_whenCheckForPdf_thenEmptyOptional() {
        File file = new File("src/test/resources/pdf/dummy.pdf");

        Optional<String> checkForPdf = tikaService.checkForPdf(file);

        assertTrue(checkForPdf.isEmpty());
    }

    @Test
    public void givenTextFile_whenCheckForPdf_thenTextFile() {
        File file = new File("src/test/resources/pdf/test.txt");

        Optional<String> checkForPdf = tikaService.checkForPdf(file);

        assertTrue(checkForPdf.isPresent());
        assertEquals("test.txt", checkForPdf.get());
    }

    @Test
    public void givenNoFile_whenCheckForPdf_thenException() {
        File file = new File("src/test/resources/pdf/nonexistingfile.txt");

        assertThrows(IllegalStateException.class,() -> tikaService.checkForPdf(file).orElseThrow(() -> new IllegalStateException("Oops")));
    }

}
