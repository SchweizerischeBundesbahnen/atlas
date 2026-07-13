package ch.sbb.atlas.pdf.sanitize;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

class PdfCdrTest {

  @Test
  void shouldSanitizeJavaScriptClockPdf() throws IOException {
    // given
    String filePathToSanitize = "src/test/resources/pdf/cdr/JavaScriptClock.pdf";
    String sanitizedFilePath = "src/test/resources/pdf/cdr/JavaScriptClockSanitized.pdf";

    try (InputStream inputStream = new FileInputStream(filePathToSanitize)) {
      String beforeSanitationMd5Checksum = DigestUtils.md5Hex(inputStream);
      assertThat(beforeSanitationMd5Checksum).isEqualTo("e2f4f2b39db7318e5d2e6a65f9fe3877");
    }

    // when
    try (InputStream inputStream = getClass().getResourceAsStream("/pdf/cdr/JavaScriptClock.pdf");
        OutputStream outputStream = new FileOutputStream(sanitizedFilePath)) {
      PdfCdr.sanitize(inputStream, outputStream);
    }

    // then
    try (InputStream inputStream = new FileInputStream(sanitizedFilePath)) {
      String afterSanitationMd5Checksum = DigestUtils.md5Hex(inputStream);
      assertThat(afterSanitationMd5Checksum).isEqualTo("91f21d7a90eb3240a1c638e44d75f65c");
    }
  }

  @Test
  void shouldSanitizeEmbeddedSimpleFormCaluclationsPdf() throws IOException {
    // given
    String filePathToSanitize = "src/test/resources/pdf/cdr/EmbeddedSimpleFormCaluclations.pdf";
    String sanitizedFilePath = "src/test/resources/pdf/cdr/EmbeddedSimpleFormCaluclationsSanitized.pdf";

    try (InputStream inputStream = new FileInputStream(filePathToSanitize)) {
      String beforeSanitationMd5Checksum = DigestUtils.md5Hex(inputStream);
      assertThat(beforeSanitationMd5Checksum).isEqualTo("bc333b0eae3f4e9d5ac44364bdc63201");
    }

    // when
    try (InputStream inputStream = getClass().getResourceAsStream("/pdf/cdr/EmbeddedSimpleFormCaluclations.pdf");
        OutputStream outputStream = new FileOutputStream(sanitizedFilePath)) {
      PdfCdr.sanitize(inputStream, outputStream);
    }

    // then
    try (InputStream inputStream = new FileInputStream(sanitizedFilePath)) {
      String afterSanitationMd5Checksum = DigestUtils.md5Hex(inputStream);
      assertThat(afterSanitationMd5Checksum).isEqualTo("567ed0eb4758010de63d4fd19728b40a");
    }
  }

}
