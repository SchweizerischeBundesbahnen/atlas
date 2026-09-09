/*
 * Portions of this file are based on DocBleach (https://github.com/docbleach/DocBleach).
 *
 * MIT License
 *
 * Copyright (c) 2017 Damien Buhl (alias daedric)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ch.sbb.atlas.pdf.sanitize;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * PDF CDR
 * Content Disarm & Reconstruction (<a href="https://en.wikipedia.org/wiki/Content_Disarm_%26_Reconstruction">see Wikipedia</a>)
 * Code based on archived <a href="https://github.com/docbleach/DocBleach">DocBleach on GitHub</a> (MIT License, see file
 * header).
 */
@Slf4j
@UtilityClass
public class PdfCdr {

  /**
   * Maximum depth of nested PDF sanitization (embedded PDF inside embedded PDF ...). This prevents infinite recursion
   * caused by maliciously crafted, deeply nested or self-referencing documents.
   */
  static final int MAX_RECURSION_DEPTH = 10;

  /**
   * Sanitizes a file and replaces its content with the sanitized content.
   * File has to be a PDF. This will not be checked explicitly.
   */
  public static void sanitize(File file) {
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

      performSanitize(Loader.loadPDF(file), byteArrayOutputStream, 0);

      try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
        byteArrayOutputStream.writeTo(fileOutputStream);
      }
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Sanitizes a stream and fills the OutputStream with the sanitized content
   * InputStream has to be a PDF. This will not be checked explicitly.
   */
  public static void sanitize(InputStream inputStream, OutputStream outputStream) {
    sanitize(inputStream, outputStream, 0);
  }

  static void sanitize(InputStream inputStream, OutputStream outputStream, int recursionDepth) {
    try {
      performSanitize(Loader.loadPDF(new RandomAccessReadBuffer(inputStream)), outputStream, recursionDepth);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  private static void performSanitize(PDDocument document, OutputStream outputStream, int recursionDepth) throws IOException {
    PdfCdrResult result = new PdfCdrRun().sanitize(document, outputStream, recursionDepth);
    log.info("Removed {} actions from PDF", result.getPerformedActions().size());
  }
}