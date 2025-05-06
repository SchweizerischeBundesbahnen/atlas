package ch.sbb.atlas.pdf.sanitize;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * PDF CDR
 * Content Disarm & Reconstruction (<a href="https://en.wikipedia.org/wiki/Content_Disarm_%26_Reconstruction">see Wikipedia</a>)
 * Code based on archived <a href="https://github.com/docbleach/DocBleach">DocBleach on GitHub</a>
 */
@Slf4j
public class PdfCdr {

  /**
   * Sanitizes a file and replaces its content with the sanitized content
   */
  public static void sanitize(File file) {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

      performSanitize(Loader.loadPDF(file), byteArrayOutputStream);

      try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
        byteArrayOutputStream.writeTo(fileOutputStream);
      }
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Sanitizes a stream and fills the OutputStream with the sanitized content
   */
  public static void sanitize(InputStream inputStream, OutputStream outputStream) {
    if (PdfFileChecker.hasPdfFileMarker(inputStream)) {
      try {
        performSanitize(Loader.loadPDF(new RandomAccessReadBuffer(inputStream)), outputStream);
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    } else {
      throw new IllegalStateException("Pdf file marker not found on input stream, file is not a pdf file");
    }
  }

  private static void performSanitize(PDDocument document, OutputStream outputStream) throws IOException {
    PdfCdrResult result = new PdfCdrRun().sanitize(document, outputStream);
    log.info("Removed {} actions from PDF", result.getPerformedActions().size());
  }
}