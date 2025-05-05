package ch.sbb.atlas.pdf.sanitize;

import java.io.IOException;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

@Slf4j
class PdfCdrRun {

  private final PdfCdrResult result = new PdfCdrResult();

  PdfCdrResult sanitize(RandomAccessRead inputStream, OutputStream outputStream) throws IOException {
    PDDocument doc = Loader.loadPDF(inputStream);

    sanitizeNamed(doc, doc.getDocumentCatalog().getNames());
    new PDDocumentCatalogBleach(result).sanitize(doc.getDocumentCatalog());

    sanitizeDocumentOutline(doc.getDocumentCatalog().getDocumentOutline());

    doc.save(outputStream);
    doc.close();
    return result;
  }

  private void sanitizeDocumentOutline(PDDocumentOutline documentOutline) {
    if (documentOutline == null) {
      return;
    }
    documentOutline.children().forEach(this::sanitizeDocumentOutlineItem);
  }

  private void sanitizeDocumentOutlineItem(PDOutlineItem item) {
    if (item.getAction() == null) {
      return;
    }
    log.debug("Found&removed action on outline item (was {})", item.getAction());
    item.setAction(null);
    result.addPerformedAction("Removed action from PDOutlineItem");
  }

  private void sanitizeNamed(PDDocument doc, PDDocumentNameDictionary names) {
    if (names == null) {
      return;
    }

    new PDEmbeddedFileBleach(doc).sanitize(names.getEmbeddedFiles());

    if (names.getJavaScript() != null) {
      names.setJavascript(null);
      result.addPerformedAction("Removed Named JavaScriptAction");
    }
  }

}
