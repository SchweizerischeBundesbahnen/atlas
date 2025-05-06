package ch.sbb.atlas.pdf.sanitize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.RandomAccessReadBuffer;

/**
 * PDF CDR
 * Content Disarm & Reconstruction (<a href="https://en.wikipedia.org/wiki/Content_Disarm_%26_Reconstruction">see Wikipedia</a>)
 * Code based on archived <a href="https://github.com/docbleach/DocBleach">DocBleach on GitHub</a>
 */
@Slf4j
public class PdfCdr {

  public static void sanitize(InputStream inputStream, OutputStream outputStream) {
    if (PdfFileChecker.hasPdfFileMarker(inputStream)) {
      try {
        PdfCdrResult result = new PdfCdrRun().sanitize(new RandomAccessReadBuffer(inputStream), outputStream);
        log.info("Removed {} actions from PDF", result.getPerformedActions().size());
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    } else {
      throw new IllegalStateException("Pdf file marker not found on input stream, file is not a pdf file");
    }
  }

}