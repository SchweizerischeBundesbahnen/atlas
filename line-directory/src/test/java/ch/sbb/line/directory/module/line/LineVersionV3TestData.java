package ch.sbb.line.directory.module.line;

import ch.sbb.atlas.api.lidi.enumaration.ConcessionTypeV3;
import ch.sbb.atlas.api.lidi.enumaration.Financing;
import ch.sbb.atlas.api.lidi.enumaration.LineMeanOfTransport;
import ch.sbb.atlas.api.lidi.enumaration.LineTypeV3;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.entity.LineVersionV3.LineVersionV3Builder;
import java.time.LocalDate;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LineVersionV3TestData {

  public static final String PARENT_SLNID = "ch:1:slnid:1028629";
  public static final String CHILD_SLNID = "ch:1:slnid:1028629:4";
  public static final String UNKNOWN_SLNID = "ch:1:slnid:9999999";

  public static LineVersionV3Builder<?, ?> lineBuilder() {
    return LineVersionV3.builder()
        .status(Status.VALIDATED)
        .slnid(PARENT_SLNID)
        .lineType(LineTypeV3.ORDERLY)
        .concessionType(ConcessionTypeV3.CANTONALLY_APPROVED_LINE)
        .number("90.583")
        .swissLineNumber("90.583")
        .shortNumber("583")
        .offerCategory(OfferCategory.B)
        .longName("Savognin - Pensa")
        .description("Val-Surses-Bus")
        .businessOrganisation("ch:1:sboid:1100048")
        .financing(Financing.SELF_FINANCED)
        .meansOfTransport(Set.of(LineMeanOfTransport.TRAIN))
        .validFrom(LocalDate.of(2026, 1, 1))
        .validTo(LocalDate.of(9999, 12, 31));
  }

  public static LineVersionV3Builder<?, ?> sublineBuilder() {
    return LineVersionV3.builder()
        .status(Status.VALIDATED)
        .slnid(CHILD_SLNID)
        .referencedLineSlnid(PARENT_SLNID)
        .lineType(LineTypeV3.TECHNICAL)
        .description("Savognin - Cre digl Lai")
        .longName("Savognin, posta - Cre digl Lai")
        .linienId("583d")
        .offerCategory(OfferCategory.B)
        .businessOrganisation("ch:1:sboid:1100048")
        .financing(Financing.WBV)
        .meansOfTransport(Set.of(LineMeanOfTransport.BUS))
        .validFrom(LocalDate.of(2026, 1, 1))
        .validTo(LocalDate.of(9999, 12, 31));
  }

  public static LineVersionV3 line() {
    return lineBuilder().build();
  }

  public static LineVersionV3 subline() {
    return sublineBuilder().build();
  }

}
