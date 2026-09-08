package ch.sbb.atlas.pdf.sanitize;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;

@Slf4j
class PdfCdrNamesSanitization extends PdfCdrReporter {

  PdfCdrNamesSanitization(PdfCdrResult result) {
    super(result);
  }

  void sanitizeNamed(PDDocument doc, PDDocumentNameDictionary names, int recursionDepth) {
    if (names == null) {
      return;
    }

    new PdfCdrEmbeddedFileSanitization(doc, recursionDepth).sanitize(names.getEmbeddedFiles());

    if (names.getJavaScript() != null) {
      names.setJavascript(null);
      reportPerformedAction("Removed Named JavaScriptAction");
    }
  }
}
