package ch.sbb.atlas.pdf.sanitize;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;
import org.apache.pdfbox.pdmodel.interactive.action.PDDocumentCatalogAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.action.PDFormFieldAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.action.PDPageAdditionalActions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PdfCdrDocumentCatalogSanitizationTest {

  private PdfCdrDocumentCatalogSanitization pdfCdrDocumentCatalogSanitization;
  private PdfCdrResult result;

  @BeforeEach
  void setUp() {
    result = new PdfCdrResult();
    pdfCdrDocumentCatalogSanitization = new PdfCdrDocumentCatalogSanitization(result);
  }


  @Test
  void shouldSanitizeAcroFormActions() {
    PDFormFieldAdditionalActions fieldActions = new PDFormFieldAdditionalActions();
    fieldActions.setC(new PDActionJavaScript());
    fieldActions.setF(new PDActionJavaScript());
    fieldActions.setK(new PDActionJavaScript());
    fieldActions.setV(new PDActionJavaScript());

    pdfCdrDocumentCatalogSanitization.sanitizeFieldAdditionalActions(fieldActions);
    assertThat(result.getPerformedActions()).hasSize(4);

    assertThat(fieldActions.getC()).isNull();
    assertThat(fieldActions.getF()).isNull();
    assertThat(fieldActions.getK()).isNull();
    assertThat(fieldActions.getV()).isNull();

    result.getPerformedActions().clear();

    pdfCdrDocumentCatalogSanitization.sanitizeFieldAdditionalActions(fieldActions);
    assertThat(result.getPerformedActions()).isEmpty();
  }


  @Test
  void shouldSanitizePageActions() {
    PDPageAdditionalActions actions = new PDPageAdditionalActions();
    actions.setC(new PDActionJavaScript());
    actions.setO(new PDActionJavaScript());

    pdfCdrDocumentCatalogSanitization.sanitizePageActions(actions);
    assertThat(result.getPerformedActions()).hasSize(2);

    assertThat(actions.getC()).isNull();
    assertThat(actions.getO()).isNull();

    result.getPerformedActions().clear();

    pdfCdrDocumentCatalogSanitization.sanitizePageActions(actions);
    assertThat(result.getPerformedActions()).isEmpty();
  }

  @Test
  void shouldSanitizeAdditionalActions() {
    PDDocumentCatalogAdditionalActions documentCatalogAdditionalActions =
        new PDDocumentCatalogAdditionalActions();
    pdfCdrDocumentCatalogSanitization.sanitizeDocumentActions(documentCatalogAdditionalActions);
    documentCatalogAdditionalActions.setDP(new PDActionJavaScript());
    documentCatalogAdditionalActions.setDS(new PDActionJavaScript());
    documentCatalogAdditionalActions.setWC(new PDActionJavaScript());
    documentCatalogAdditionalActions.setWP(new PDActionJavaScript());
    documentCatalogAdditionalActions.setWS(new PDActionJavaScript());

    pdfCdrDocumentCatalogSanitization.sanitizeDocumentActions(documentCatalogAdditionalActions);
    assertThat(result.getPerformedActions()).hasSize(5);
    result.getPerformedActions().clear();

    assertThat(documentCatalogAdditionalActions.getDP()).isNull();
    assertThat(documentCatalogAdditionalActions.getDS()).isNull();
    assertThat(documentCatalogAdditionalActions.getWC()).isNull();
    assertThat(documentCatalogAdditionalActions.getWP()).isNull();
    assertThat(documentCatalogAdditionalActions.getWS()).isNull();

    pdfCdrDocumentCatalogSanitization.sanitizeDocumentActions(documentCatalogAdditionalActions);
    assertThat(result.getPerformedActions()).isEmpty();
  }

  @Test
  void shouldSanitizeOpenAction() throws IOException {
    PDDocumentCatalog documentCatalog = mock(PDDocumentCatalog.class);

    when(documentCatalog.getOpenAction()).thenReturn(new PDActionJavaScript());
    pdfCdrDocumentCatalogSanitization.sanitizeOpenAction(documentCatalog);

    verify(documentCatalog, atLeastOnce()).getOpenAction();
    verify(documentCatalog, atLeastOnce()).setOpenAction(null);
    assertThat(result.getPerformedActions()).hasSize(1);

    result.getPerformedActions().clear();
    reset(documentCatalog);

    when(documentCatalog.getOpenAction()).thenReturn(null);
    pdfCdrDocumentCatalogSanitization.sanitizeOpenAction(documentCatalog);
    verify(documentCatalog, atLeastOnce()).getOpenAction();
    verify(documentCatalog, never()).setOpenAction(null);

    assertThat(result.getPerformedActions()).isEmpty();
  }

}