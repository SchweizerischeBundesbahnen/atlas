package ch.sbb.atlas.pdf.sanitize;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract class PdfCdrReporter {

  private final PdfCdrResult result;

  protected void reportPerformedAction(String action) {
    result.addPerformedAction(action);
  }

}
