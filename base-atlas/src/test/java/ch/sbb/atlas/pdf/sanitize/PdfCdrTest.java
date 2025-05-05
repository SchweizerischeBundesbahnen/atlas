package ch.sbb.atlas.pdf.sanitize;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.junit.jupiter.api.Test;

class PdfCdrTest {

  @Test
  void shouldCompressAndDecompressS3ObjectInputStream() throws IOException {
    try (InputStream inputStream = getClass().getResourceAsStream("/JavaScriptClock.pdf");
        OutputStream outputStream = new FileOutputStream("JavaScriptClockSanitized.pdf")) {

      PdfCdr.sanitize(inputStream, outputStream);
    }

  }

}