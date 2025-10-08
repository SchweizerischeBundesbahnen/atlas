package ch.sbb.line.directory.module.line;

import ch.sbb.atlas.api.lidi.LineVersionModelV2;
import ch.sbb.atlas.api.lidi.LineVersionModelV2.LineVersionModelV2Builder;
import ch.sbb.atlas.api.lidi.UpdateLineVersionModelV2;
import ch.sbb.atlas.api.lidi.UpdateLineVersionModelV2.UpdateLineVersionModelV2Builder;
import ch.sbb.atlas.api.lidi.enumaration.LidiElementType;
import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.module.line.entity.Line;
import ch.sbb.line.directory.module.line.entity.Line.LineBuilder;
import ch.sbb.line.directory.module.line.entity.LineVersion;
import ch.sbb.line.directory.module.line.entity.LineVersion.LineVersionBuilder;
import java.time.LocalDate;
import java.util.Collections;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LineTestData {

  public static LineVersionBuilder<?, ?> lineVersionBuilder() {
    return LineVersion.builder()
        .status(Status.VALIDATED)
        .lineType(LineType.ORDERLY)
        .number("number")
        .longName("longName")
        .description("description")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .businessOrganisation("businessOrganisation")
        .comment("comment")
        .swissLineNumber("swissLineNumber");
  }

  public static LineVersionBuilder<?, ?> lineVersionV2Builder() {
    return lineVersionBuilder()
        .concessionType(LineConcessionType.COLLECTION_LINE)
        .shortNumber("6")
        .offerCategory(OfferCategory.IC);
  }

  public static LineVersion lineVersion() {
    return lineVersionBuilder().build();
  }

  public static LineVersionModelV2Builder<?, ?> createLineVersionModelBuilder() {
    return LineVersionModelV2.builder()
        .status(Status.VALIDATED)
        .lineType(LineType.ORDERLY)
        .lineConcessionType(LineConcessionType.COLLECTION_LINE)
        .offerCategory(OfferCategory.IC)
        .shortNumber("6")
        .number("number")
        .longName("longName")
        .description("description")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .businessOrganisation("businessOrganisation")
        .comment("comment")
        .lineVersionWorkflows(Collections.emptySet())
        .swissLineNumber("swissLineNumber");
  }

  public static UpdateLineVersionModelV2Builder<?, ?> updateLineVersionModelBuilder() {
    return UpdateLineVersionModelV2.builder()
        .status(Status.VALIDATED)
        .lineConcessionType(LineConcessionType.COLLECTION_LINE)
        .offerCategory(OfferCategory.IC)
        .shortNumber("6")
        .number("number")
        .longName("longName")
        .description("description")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .businessOrganisation(
            "businessOrganisation")
        .comment("comment")
        .lineVersionWorkflows(Collections.emptySet())
        .swissLineNumber("swissLineNumber");
  }

  public static LineBuilder lineBuilder() {
    return Line.builder()
        .status(Status.VALIDATED)
        .lidiElementType(LidiElementType.CONCESSION)
        .number("number")
        .description("description")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .businessOrganisation(
            "businessOrganisation")
        .swissLineNumber("swissLineNumber");
  }

  public static Line line() {
    return lineBuilder().build();
  }
}
