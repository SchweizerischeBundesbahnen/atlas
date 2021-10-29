package ch.sbb.line.directory;

import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.enumaration.SublineType;
import java.time.LocalDate;

public class SublineTestData {

  public static SublineVersion sublineVersion() {
    return SublineVersion.builder()
                         .status(Status.ACTIVE)
                         .type(SublineType.TECHNICAL)
                         .paymentType(
                             PaymentType.INTERNATIONAL)
                         .number("number")
                         .longName("longName")
                         .description("description")
                         .validFrom(
                             LocalDate.of(2020, 12, 12))
                         .validTo(
                             LocalDate.of(2099, 12, 12))
                         .businessOrganisation(
                             "businessOrganisation")
                         .swissLineNumber("swissLineNumber")
                         .swissSublineNumber(
                             "swissSublineNumber")
                         .build();
  }
}
