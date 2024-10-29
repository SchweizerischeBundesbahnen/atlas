package ch.sbb.line.directory.controller;

import ch.sbb.atlas.api.lidi.SublineApiV2;
import ch.sbb.atlas.api.lidi.SublineVersionModelV2;
import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import ch.sbb.atlas.model.Status;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SublineControllerV2 implements SublineApiV2 {

  public static final LocalDate VALID_FROM = LocalDate.of(2020, 1, 1);
  public static final LocalDate VALID_TO = LocalDate.of(9999, 12, 31);

  @Override
  public List<SublineVersionModelV2> getSublineVersion(String slnid) {
    return List.of(getSublineVersionV2DraftModel());
  }

  private SublineVersionModelV2 getSublineVersionV2DraftModel() {
    return SublineVersionModelV2.builder()
        .id(1L)
        .slnid("ch:1:slnid:1024336:1")
        .mainlineSlnid("ch:1:slnid:1024336")
        .swissSublineNumber("b0.IC6:EC")
        .businessOrganisation("ch:1:slnid:1024336")
        .status(Status.VALIDATED)
        .lineConcessionType(LineConcessionType.LINE_OF_A_TERRITORIAL_CONCESSION)
        .sublineType(SublineType.DISPOSITION)
        .shortNumber("61")
        .offerCategory("IC")
        .longName("longName")
        .description("Basel SBB - Olten - Bern - LBT - Brig - Domodossola")
        .validFrom(VALID_FROM)
        .validTo(VALID_TO)
        .status(Status.VALIDATED)
        .mainSwissLineNumber("b0.IC6")
        .creationDate(LocalDateTime.now())
        .creator("Calipso")
        .editionDate(LocalDateTime.now())
        .editor("Neptun")
        .build();
  }
}
