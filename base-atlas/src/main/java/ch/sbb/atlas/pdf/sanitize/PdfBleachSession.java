package ch.sbb.atlas.pdf.sanitize;

import java.io.IOException;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

@Slf4j
class PdfBleachSession {

  void sanitize(RandomAccessRead source, OutputStream outputStream) throws IOException {
    final PDDocument doc = getDocument(source);

    final PDDocumentCatalog docCatalog = doc.getDocumentCatalog();

    sanitizeNamed(doc, docCatalog.getNames());
    PDDocumentCatalogBleach catalogBleach = new PDDocumentCatalogBleach();
    catalogBleach.sanitize(docCatalog);
    sanitizeDocumentOutline(doc.getDocumentCatalog().getDocumentOutline());

    new COSObjectBleach().sanitizeObjects(doc.getDocument());

    doc.save(outputStream);
    doc.close();
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
  }

  private void sanitizeNamed(PDDocument doc, PDDocumentNameDictionary names) {
    if (names == null) {
      return;
    }

    new PDEmbeddedFileBleach(doc).sanitize(names.getEmbeddedFiles());

    if (names.getJavaScript() != null) {
      names.setJavascript(null);
    }
  }

  private PDDocument getDocument(RandomAccessRead source) throws IOException {
    PDFParser parser = new PDFParser(source);
    try (PDDocument document = parser.parse()) {
      return document;
    }
  }

}
