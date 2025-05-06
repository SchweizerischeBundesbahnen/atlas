package ch.sbb.atlas.pdf.sanitize;

import java.io.IOException;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdmodel.PDDocument;

@Slf4j
class PdfCdrRun {

  PdfCdrResult sanitize(RandomAccessRead inputStream, OutputStream outputStream) throws IOException {
    PDDocument doc = Loader.loadPDF(inputStream);

    PdfCdrResult result = new PdfCdrResult();
    new PdfCdrNamesSanitization(result).sanitizeNamed(doc, doc.getDocumentCatalog().getNames());
    new PdfCdrDocumentCatalogSanitization(result).sanitize(doc.getDocumentCatalog());
    new PdfCdrDocumentOutlineSanitization(result).sanitizeDocumentOutline(doc.getDocumentCatalog().getDocumentOutline());

    doc.save(outputStream);
    doc.close();
    return result;
  }

}
