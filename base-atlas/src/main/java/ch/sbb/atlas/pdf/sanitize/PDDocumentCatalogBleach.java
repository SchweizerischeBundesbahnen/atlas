package ch.sbb.atlas.pdf.sanitize;

import java.io.IOException;
import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDDestinationOrAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDDocumentCatalogAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.action.PDFormFieldAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.action.PDPageAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

@Slf4j
class PDDocumentCatalogBleach {

  private final PDAnnotationBleach annotationBleach;

  PDDocumentCatalogBleach() {
    this.annotationBleach = new PDAnnotationBleach();
  }


  private void sanitizeAcroFormActions(PDAcroForm acroForm) {
    if (acroForm == null) {
      log.debug("No AcroForms found");
      return;
    }
    log.trace("Checking AcroForm Actions");

    Iterator<PDField> fields = acroForm.getFieldIterator();

    fields.forEachRemaining(this::sanitizeField);
  }

  private void sanitizeField(PDField field) {
    // Sanitize annotations
    field.getWidgets().forEach(annotationBleach::sanitizeAnnotation);

    // Sanitize field actions
    PDFormFieldAdditionalActions fieldActions = field.getActions();
    if (fieldActions == null) {
      return;
    }
    sanitizeFieldAdditionalActions(fieldActions);
  }

  private void sanitizePage(PDPage page) throws IOException {
    for (PDAnnotation annotation : page.getAnnotations()) {
      annotationBleach.sanitizeAnnotation(annotation);
      sanitizePageActions(page.getActions());
    }
  }

  private void sanitizePageActions(PDPageTree pages) throws IOException {
    log.trace("Checking Pages Actions");
    for (PDPage page : pages) {
      sanitizePage(page);
    }
  }

  void sanitizePageActions(PDPageAdditionalActions pageActions) {
    if (pageActions.getC() != null) {
      log.debug("Found&removed action when page is closed, was ({})", pageActions.getC());
      pageActions.setC(null);
    }

    if (pageActions.getO() != null) {
      log.debug("Found&removed action when page is opened, was ({})", pageActions.getO());
      pageActions.setO(null);
    }
  }

  void sanitizeOpenAction(PDDocumentCatalog docCatalog)
      throws IOException {
    log.trace("Checking OpenAction...");
    PDDestinationOrAction openAction = docCatalog.getOpenAction();

    if (openAction == null) {
      return;
    }

    log.debug("Found a JavaScript OpenAction, removed. Was {}", openAction);
    docCatalog.setOpenAction(null);
  }

  void sanitizeDocumentActions(PDDocumentCatalogAdditionalActions documentActions) {
    log.trace("Checking additional actions...");
    if (documentActions.getDP() != null) {
      log.debug("Found&removed action after printing (was {})", documentActions.getDP());
      documentActions.setDP(null);
    }
    if (documentActions.getDS() != null) {
      log.debug("Found&removed action after saving (was {})", documentActions.getDS());
      documentActions.setDS(null);
    }
    if (documentActions.getWC() != null) {
      log.debug("Found&removed action before closing (was {}", documentActions.getWC());
      documentActions.setWC(null);
    }
    if (documentActions.getWP() != null) {
      log.debug("Found&removed action before printing (was {})", documentActions.getWP());
      documentActions.setWP(null);
    }
    if (documentActions.getWS() != null) {
      log.debug("Found&removed action before saving (was {})", documentActions.getWS());
      documentActions.setWS(null);
    }
  }

  void sanitizeFieldAdditionalActions(PDFormFieldAdditionalActions fieldActions) {
    if (fieldActions.getC() != null) {
      log.debug(
          "Found&removed an action to be performed in order to recalculate the value of this field when that of another field changes.");
      fieldActions.setC(null);
    }
    if (fieldActions.getF() != null) {
      log.debug(
          "Found&removed an action to be performed before the field is formatted to display its current value.");
      fieldActions.setF(null);
    }
    if (fieldActions.getK() != null) {
      log.debug(
          "Found&removed an action to be performed when the user types a keystroke into a text field or combo box or modifies the selection in a scrollable list box.");
      fieldActions.setK(null);
    }
    if (fieldActions.getV() != null) {
      log.debug(
          "Found&removed an action to be action to be performed when the field's value is changed.");
      fieldActions.setV(null);
    }
  }

  void sanitize(PDDocumentCatalog docCatalog) throws IOException {
    sanitizeOpenAction(docCatalog);
    sanitizeDocumentActions(docCatalog.getActions());
    sanitizePageActions(docCatalog.getPages());
    sanitizeAcroFormActions(docCatalog.getAcroForm());
  }
}
