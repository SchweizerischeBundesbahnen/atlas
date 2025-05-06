package ch.sbb.atlas.pdf.sanitize;

import java.io.IOException;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;

@Slf4j
class PdfCdrRun {

  PdfCdrResult sanitize(PDDocument doc, OutputStream outputStream) throws IOException {
    PdfCdrResult result = new PdfCdrResult();
    new PdfCdrNamesSanitization(result).sanitizeNamed(doc, doc.getDocumentCatalog().getNames());
    new PdfCdrDocumentCatalogSanitization(result).sanitize(doc.getDocumentCatalog());
    new PdfCdrDocumentOutlineSanitization(result).sanitizeDocumentOutline(doc.getDocumentCatalog().getDocumentOutline());

    doc.save(outputStream);
    doc.close();
    return result;
  }

}
