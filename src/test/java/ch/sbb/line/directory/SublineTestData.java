package ch.sbb.line.directory;

import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.entity.SublineVersion.SublineVersionBuilder;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.enumaration.SublineType;
import java.time.LocalDate;

public class SublineTestData {

  public static SublineVersionBuilder sublineVersionBuilder() {
    return SublineVersion.builder()
                         .status(Status.ACTIVE)
                         .type(SublineType.TECHNICAL)
                         .paymentType(
                             PaymentType.INTERNATIONAL)
                         .number("number")
                         .longName("longName")
                         .description("description")
                         .validFrom(
                             LocalDate.of(2020, 1, 1))
                         .validTo(
                             LocalDate.of(2020, 12, 31))
                         .businessOrganisation(
                             "businessOrganisation")
                         .swissLineNumber("swissLineNumber")
                         .swissSublineNumber(
                             "swissSublineNumber");
  }

  public static SublineVersion sublineVersion() {
    return sublineVersionBuilder().build();
  }
}
