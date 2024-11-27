package ch.sbb.line.directory;

import ch.sbb.atlas.api.lidi.SublineVersionModel;
import ch.sbb.atlas.api.lidi.SublineVersionModel.SublineVersionModelBuilder;
import ch.sbb.atlas.api.lidi.enumaration.PaymentType;
import ch.sbb.atlas.api.lidi.enumaration.SublineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.entity.SublineVersion.SublineVersionBuilder;
import java.time.LocalDate;

public class SublineTestData {

  public static final String MAINLINE_SLNID = "ch:1:slnid:1000546";

  public static SublineVersionBuilder<?, ?> sublineVersionBuilder() {
    return SublineVersion.builder()
        .status(Status.VALIDATED)
        .sublineType(SublineType.TECHNICAL)
        .paymentType(PaymentType.INTERNATIONAL)
        .number("number")
        .longName("longName")
        .description("description")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .businessOrganisation("businessOrganisation")
        .mainlineSlnid(MAINLINE_SLNID)
        .swissSublineNumber("swissSublineNumber");
  }

  public static SublineVersionBuilder<?, ?> sublineVersionV2Builder() {
    return sublineVersionBuilder().concessionType(SublineConcessionType.LINE_ABROAD);
  }

  public static SublineVersion sublineVersion() {
    return sublineVersionBuilder().build();
  }

  public static SublineVersionModelBuilder<?, ?> sublineVersionModelBuilder() {
    return SublineVersionModel.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .businessOrganisation("sbb")
        .swissSublineNumber("b0.Ic2-sibline")
        .number("number")
        .description("description")
        .sublineType(SublineType.TECHNICAL)
        .paymentType(PaymentType.LOCAL)
        .mainlineSlnid(MAINLINE_SLNID);
  }
}
