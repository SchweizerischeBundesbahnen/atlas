package ch.sbb.line.directory.controller;

import ch.sbb.atlas.api.lidi.LineApiV2;
import ch.sbb.atlas.api.lidi.LineVersionModelV2;
import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.model.Status;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LineControllerV2 implements LineApiV2 {

  public static final LocalDate VALID_FROM = LocalDate.of(2020, 1, 1);
  public static final LocalDate VALID_TO = LocalDate.of(9999, 12, 31);

  @Override
  public List<LineVersionModelV2> getLineVersions(String slnid) {
    return List.of(getLineVersionV2DraftModel());
  }

  private LineVersionModelV2 getLineVersionV2DraftModel() {
    return LineVersionModelV2.builder()
        .id(1L)
        .slnid("ch:1:slnid:1024336")
        .status(Status.VALIDATED)
        .lineType(LineType.DISPOSITION)
        .lineConcessionType(LineConcessionType.LINE_OF_A_TERRITORIAL_CONCESSION)
        .shortNumber("61")
        .offerCategory(OfferCategory.IC)
        .number("IC61")
        .alternativeName("alternativeName")
        .combinationName("combinationName")
        .longName("longName")
        .colorFontRgb("#FFFFFF")
        .colorBackRgb("#FFFFFF")
        .colorFontCmyk("0,0,0,0")
        .colorBackCmyk("0,0,0,0")
        .description("Basel SBB - Olten - Bern - LBT - Brig - Domodossola")
        .validFrom(VALID_FROM)
        .validTo(VALID_TO)
        .businessOrganisation("ch:1:sboid:123")
        .comment("""
            Frankfurt - Basel SBB und Brig - Milano nur teilweise
            Konzessionsrecht gilt nur für den schweizerischen Linienabschnitt
            Einzelzüge via Lötschberg-Bergstrecke
            """)
        .status(Status.VALIDATED)
        .lineVersionWorkflows(Collections.emptySet())
        .swissLineNumber("b0.IC6")
        .creationDate(LocalDateTime.now())
        .creator("Calipso")
        .editionDate(LocalDateTime.now())
        .editor("Neptun")
        .build();
  }
}
