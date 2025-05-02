package ch.sbb.atlas.pdf.sanitize;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.interactive.action.PDAnnotationAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;

@Slf4j
class PDAnnotationBleach {

  void sanitizeLinkAnnotation(PDAnnotationLink annotationLink) {
    if (annotationLink.getAction() == null) {
      return;
    }
    log.debug("Found&removed annotation link - action, was {}", annotationLink.getAction());
    annotationLink.setAction(null);
  }

  void sanitizeWidgetAnnotation(PDAnnotationWidget annotationWidget) {
    if (annotationWidget.getAction() != null) {
      log.debug(
          "Found&Removed action on annotation widget, was {}", annotationWidget.getAction());
      annotationWidget.setAction(null);
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
    }
    if (annotationAdditionalActions.getD() != null) {
      log.debug(
          "Found&Removed action on annotation widget to be performed when the mouse button is pressed inside the annotation's active area, was {}",
          annotationAdditionalActions.getD());
      annotationAdditionalActions.setD(null);
    }
    if (annotationAdditionalActions.getE() != null) {
      log.debug(
          "Found&Removed action on annotation widget to be performed when the cursor enters the annotation's active area, was {}",
          annotationAdditionalActions.getE());
      annotationAdditionalActions.setE(null);
    }
    if (annotationAdditionalActions.getFo() != null) {
      log.debug(
          "Found&Removed action on annotation widget to be performed when the annotation receives the input focus, was {}",
          annotationAdditionalActions.getFo());
      annotationAdditionalActions.setFo(null);
    }
    if (annotationAdditionalActions.getPC() != null) {
      log.debug(
          "Found&Removed action on annotation widget to be performed when the page containing the annotation is closed, was {}",
          annotationAdditionalActions.getPC());
      annotationAdditionalActions.setPC(null);
    }
    if (annotationAdditionalActions.getPI() != null) {
      log.debug(
          "Found&Removed action on annotation widget to be performed when the page containing the annotation is no longer visible in the viewer application's user interface, was {}",
          annotationAdditionalActions.getPI());
      annotationAdditionalActions.setPI(null);
    }
    if (annotationAdditionalActions.getPO() != null) {
      log.debug(
          "Found&Removed action on annotation widget to be performed when the page containing the annotation is opened, was {}",
          annotationAdditionalActions.getPO());
      annotationAdditionalActions.setPO(null);
    }
    if (annotationAdditionalActions.getPV() != null) {
      log.debug(
          "Found&Removed action on annotation widget to be performed when the page containing the annotation becomes visible in the viewer application's user interface, was {}",
          annotationAdditionalActions.getPV());
      annotationAdditionalActions.setPV(null);
    }
    if (annotationAdditionalActions.getU() != null) {
      log.debug(
          "Found&Removed action on annotation widget to be performed when the mouse button is released inside the annotation's active area, was {}",
          annotationAdditionalActions.getU());
      annotationAdditionalActions.setU(null);
    }
    if (annotationAdditionalActions.getX() != null) {
      log.debug(
          "Found&Removed action on annotation widget to be performed when the cursor exits the annotation's active area, was {}",
          annotationAdditionalActions.getX());
      annotationAdditionalActions.setX(null);
    }
  }

  void sanitizeAnnotation(PDAnnotation annotation) {
    if (annotation instanceof PDAnnotationLink) {
      sanitizeLinkAnnotation((PDAnnotationLink) annotation);
    }

    if (annotation instanceof PDAnnotationWidget) {
      sanitizeWidgetAnnotation((PDAnnotationWidget) annotation);
    }
  }
}
