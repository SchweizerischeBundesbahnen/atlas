package ch.sbb.atlas.pdf.sanitize;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.digest.DigestUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
      assertThat(afterSanitationMd5Checksum).isEqualTo("5ebf875f3ecfcabeb3d57c7d6c6b7e6a");
    }
  }

  @Test
  void shouldSanitizeJavaScriptClockPdfInPlace() {
    // given
    String filePathToSanitize = "src/test/resources/pdf/cdr/InPlaceSanitize.pdf";
    File file = new File(filePathToSanitize);

    assertThatNoException().isThrownBy(() -> PdfCdr.sanitize(file));
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
      assertThat(afterSanitationMd5Checksum).isEqualTo("89fbc46e20716c4fabb32e646cea6918");
    }
  }

}