package ch.sbb.atlas.pdf.sanitize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.RandomAccessReadBuffer;

/**
 * PDF parsing is a bit tricky: everything may or may not be linked to additional actions, so we
 * need to treat each and every elements.
 */
@Slf4j
public class PdfBleach {

  private static final byte[] PDF_MAGIC = new byte[]{37, 80, 68, 70};

  public boolean handlesMagic(InputStream stream) {
    return hasHeader(stream, PDF_MAGIC);
  }

  private static boolean hasHeader(InputStream stream, byte[] header) {
    byte[] fileMagic = new byte[header.length];
    int length;

    stream.mark(header.length);

    try {
      length = stream.read(fileMagic);

      if (stream instanceof PushbackInputStream pin) {
        pin.unread(fileMagic, 0, length);
      } else {
        stream.reset();
      }
    } catch (IOException e) {
      log.warn("An exception occured", e);
      return false;
    }

    return length == header.length && Arrays.equals(fileMagic, header);
  }

  public static void sanitize(InputStream inputStream, OutputStream outputStream) {
    try {
      new PdfBleachSession().sanitize(new RandomAccessReadBuffer(inputStream), outputStream);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

}