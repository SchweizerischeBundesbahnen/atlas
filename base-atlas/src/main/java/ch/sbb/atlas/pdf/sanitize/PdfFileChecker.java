package ch.sbb.atlas.pdf.sanitize;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Arrays;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
class PdfFileChecker {

  // PDFs start with 0x25 0x50 0x44 0x46 (in hex format, in ASCII it's %PDF)
  // This file marker is in dec format.
  private static final byte[] PDF_FILE_MARKER = new byte[]{37, 80, 68, 70};

  static boolean hasPdfFileMarker(InputStream stream) {
    return hasHeader(stream, PDF_FILE_MARKER);
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

}