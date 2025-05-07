package ch.sbb.atlas.pdf.sanitize;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.interactive.action.PDAnnotationAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;

@Slf4j
class PdfCdrAnnotationSanitization extends PdfCdrReporter {

  PdfCdrAnnotationSanitization(PdfCdrResult result) {
    super(result);
  }

  void sanitizeAnnotation(PDAnnotation annotation) {
    if (annotation instanceof PDAnnotationLink annotationLink) {
      sanitizeLinkAnnotation(annotationLink);
    }

    if (annotation instanceof PDAnnotationWidget annotationWidget) {
      sanitizeWidgetAnnotation(annotationWidget);
    }
  }

  void sanitizeLinkAnnotation(PDAnnotationLink annotationLink) {
    if (annotationLink.getAction() == null) {
      return;
    }
    log.debug("Found&removed annotation link - action, was {}", annotationLink.getAction());
    annotationLink.setAction(null);
    reportPerformedAction("Removed annotation link");
  }

  void sanitizeWidgetAnnotation(PDAnnotationWidget annotationWidget) {
    if (annotationWidget.getAction() != null) {
      log.debug(
          "Found&Removed action on annotation widget, was {}", annotationWidget.getAction());
      annotationWidget.setAction(null);
      reportPerformedAction("Removed action on PDAnnotationWidget");
    }
    sanitizeAnnotationActions(annotationWidget.getActions());
  }

  void sanitizeAnnotationActions(PDAnnotationAdditionalActions annotationAdditionalActions) {
    if (annotationAdditionalActions == null) {
      return;
    }

    if (annotationAdditionalActions.getBl() != null) {
      log.debug(
          "Found&Removed action on annotation widget to be performed when the annotation loses the input focus, was {}",
          annotationAdditionalActions.getBl());
      annotationAdditionalActions.setBl(null);
      reportPerformedAction("Removed action input focus loss on PDAnnotationAdditionalActions");
    }
    if (annotationAdditionalActions.getD() != null) {
      log.debug(
          "Found&Removed action on annotation widget to be performed when the mouse button is pressed inside the annotation's "
              + "active area, was {}",
          annotationAdditionalActions.getD());
      annotationAdditionalActions.setD(null);
      reportPerformedAction("Removed action on mouse button on PDAnnotationAdditionalActions");
    }
    if (annotationAdditionalActions.getE() != null) {
      log.debug(
          "Found&Removed action on annotation widget to be performed when the cursor enters the annotation's active area, was {}",
          annotationAdditionalActions.getE());
      annotationAdditionalActions.setE(null);
      reportPerformedAction("Removed action on cursor enter on PDAnnotationAdditionalActions");
    }
    if (annotationAdditionalActions.getFo() != null) {
      log.debug(
          "Found&Removed action on annotation widget to be performed when the annotation receives the input focus, was {}",
          annotationAdditionalActions.getFo());
      annotationAdditionalActions.setFo(null);
      reportPerformedAction("Removed action on input focus on PDAnnotationAdditionalActions");
    }
    if (annotationAdditionalActions.getPC() != null) {
      log.debug(
          "Found&Removed action on annotation widget to be performed when the page containing the annotation is closed, was {}",
          annotationAdditionalActions.getPC());
      annotationAdditionalActions.setPC(null);
      reportPerformedAction("Removed action on page close on PDAnnotationAdditionalActions");
    }
    if (annotationAdditionalActions.getPI() != null) {
      log.debug(
          "Found&Removed action on annotation widget to be performed when the page containing the annotation is no longer "
              + "visible in the viewer application's user interface, was {}",
          annotationAdditionalActions.getPI());
      annotationAdditionalActions.setPI(null);
      reportPerformedAction("Removed action on page leave on PDAnnotationAdditionalActions");
    }
    if (annotationAdditionalActions.getPO() != null) {
      log.debug(
          "Found&Removed action on annotation widget to be performed when the page containing the annotation is opened, was {}",
          annotationAdditionalActions.getPO());
      annotationAdditionalActions.setPO(null);
      reportPerformedAction("Removed action on page open on PDAnnotationAdditionalActions");
    }
    if (annotationAdditionalActions.getPV() != null) {
      log.debug(
          "Found&Removed action on annotation widget to be performed when the page containing the annotation becomes visible in"
              + " the viewer application's user interface, was {}",
          annotationAdditionalActions.getPV());
      annotationAdditionalActions.setPV(null);
      reportPerformedAction("Removed action on page enter on PDAnnotationAdditionalActions");
    }
    if (annotationAdditionalActions.getU() != null) {
      log.debug(
          "Found&Removed action on annotation widget to be performed when the mouse button is released inside the annotation's "
              + "active area, was {}",
          annotationAdditionalActions.getU());
      annotationAdditionalActions.setU(null);
      reportPerformedAction("Removed action on mouse up on PDAnnotationAdditionalActions");
    }
    if (annotationAdditionalActions.getX() != null) {
      log.debug(
          "Found&Removed action on annotation widget to be performed when the cursor exits the annotation's active area, was {}",
          annotationAdditionalActions.getX());
      annotationAdditionalActions.setX(null);
      reportPerformedAction("Removed action on cursor leave on PDAnnotationAdditionalActions");
    }
  }

}
