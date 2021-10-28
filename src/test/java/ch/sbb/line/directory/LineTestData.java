package ch.sbb.line.directory;

import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.model.CmykColor;
import ch.sbb.line.directory.model.RgbColor;
import java.time.LocalDate;

public class LineTestData {

  private static final RgbColor RGB_COLOR = new RgbColor(0, 0, 0);
  private static final CmykColor CYMK_COLOR = new CmykColor(0, 0, 0, 0);

  public static LineVersion lineVersion() {
    return LineVersion.builder()
                      .status(Status.ACTIVE)
                      .type(LineType.ORDERLY)
                      .paymentType(PaymentType.INTERNATIONAL)
                      .shortName("shortName")
                      .alternativeName("alternativeName")
                      .combinationName("combinationName")
                      .longName("longName")
                      .colorFontRgb(RGB_COLOR)
                      .colorBackRgb(RGB_COLOR)
                      .colorFontCmyk(CYMK_COLOR)
                      .colorBackCmyk(CYMK_COLOR)
                      .description("description")
                      .validFrom(LocalDate.of(2020, 12, 12))
                      .validTo(LocalDate.of(2099, 12, 12))
                      .businessOrganisation(
                          "businessOrganisation")
                      .comment("comment")
                      .swissLineNumber("swissLineNumber")
                      .build();
  }
}
