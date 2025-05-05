package ch.sbb.atlas.pdf.sanitize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.RandomAccessReadBuffer;

/**
 * PDF CDR
 * CDR = Content Disarm & Reconstruction (https://en.wikipedia.org/wiki/Content_Disarm_%26_Reconstruction)
 * Code based on archived https://github.com/docbleach/DocBleach
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
      throw new IllegalStateException("Pdf file marker not found on input stream");
    }
  }

}