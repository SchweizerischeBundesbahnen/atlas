package ch.sbb.atlas.pdf.sanitize;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

@Slf4j
class PdfCdrDocumentOutlineSanitization extends PdfCdrReporter {

  PdfCdrDocumentOutlineSanitization(PdfCdrResult result) {
    super(result);
  }

  void sanitizeDocumentOutline(PDDocumentOutline documentOutline) {
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
    reportPerformedAction("Removed action from PDOutlineItem");
  }
}
