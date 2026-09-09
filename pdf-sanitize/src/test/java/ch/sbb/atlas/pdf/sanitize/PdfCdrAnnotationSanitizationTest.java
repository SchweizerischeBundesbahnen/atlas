package ch.sbb.atlas.pdf.sanitize;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;
import org.apache.pdfbox.pdmodel.interactive.action.PDAnnotationAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PdfCdrAnnotationSanitizationTest {

  private PdfCdrAnnotationSanitization pdfCdrAnnotationSanitization;
  private PdfCdrResult result;

  @BeforeEach
  void setUp() {
    result = new PdfCdrResult();
    pdfCdrAnnotationSanitization = new PdfCdrAnnotationSanitization(result);
  }


  @Test
  void shouldSanitizeAnnotationLink() {
    PDAnnotationLink annotationLink = new PDAnnotationLink();
    annotationLink.setAction(new PDActionJavaScript());
    pdfCdrAnnotationSanitization.sanitizeLinkAnnotation(annotationLink);

    assertThat(result.getPerformedActions()).hasSize(1);
    assertThat(annotationLink.getAction()).isNull();

    result.getPerformedActions().clear();

    pdfCdrAnnotationSanitization.sanitizeLinkAnnotation(annotationLink);
    assertThat(result.getPerformedActions()).isEmpty();
  }

  @Test
  void shouldSanitizeAnnotationWidgetAction() {
    PDAnnotationWidget annotationWidget = new PDAnnotationWidget();
    annotationWidget.setAction(new PDActionJavaScript());

    pdfCdrAnnotationSanitization.sanitizeWidgetAnnotation(annotationWidget);

    assertThat(result.getPerformedActions()).hasSize(1);
    assertThat(annotationWidget.getAction()).isNull();
    result.getPerformedActions().clear();

    pdfCdrAnnotationSanitization.sanitizeWidgetAnnotation(annotationWidget);
    assertThat(result.getPerformedActions()).isEmpty();
  }

  @Test
  void shouldSanitizeAnnotationWidgetActions() throws ReflectiveOperationException {
    final String[] methods = {"Bl", "D", "E", "Fo", "PC", "PI", "PO", "U", "X"};
    final PDAction action = new PDActionJavaScript();
    final Class<PDAnnotationAdditionalActions> clazz = PDAnnotationAdditionalActions.class;

    for (String name : methods) {
      PDAnnotationAdditionalActions annotationAdditionalActions = new PDAnnotationAdditionalActions();

      Method setMethod = clazz.getMethod("set" + name, PDAction.class);
      Method getMethod = clazz.getMethod("get" + name);

      setMethod.invoke(annotationAdditionalActions, action);
      assertThat(getMethod.invoke(annotationAdditionalActions)).isNotNull();

      pdfCdrAnnotationSanitization.sanitizeAnnotationActions(annotationAdditionalActions);

      assertThat(getMethod.invoke(annotationAdditionalActions)).isNull();
      assertThat(result.getPerformedActions()).hasSize(1);
      result.getPerformedActions().clear();
    }
  }
}