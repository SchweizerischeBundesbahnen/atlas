package ch.sbb.exportservice.job.lidi.subline.processor;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.model.Status;
import ch.sbb.exportservice.integration.sql.BaseLiDiSqlIntegrationTest;
import ch.sbb.exportservice.job.lidi.line.entity.Line;
import ch.sbb.exportservice.job.lidi.subline.entity.Subline;
import java.sql.SQLException;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MainlineEnrichingProcessorIntegrationTest extends BaseLiDiSqlIntegrationTest {

  private static final String MAINLINE_SLNID = "ch:1:slnid:1";

  @Autowired
  private MainlineEnrichingProcessor mainlineEnrichingProcessor;

  @BeforeEach
  void setUp() throws SQLException {
    Line line = Line.builder()
        .id(1000L)
        .slnid(MAINLINE_SLNID)
        .validFrom(LocalDate.now())
        .validTo(LocalDate.now())
        .status(Status.VALIDATED)
        .lineType(LineType.ORDERLY)
        .concessionType(LineConcessionType.LINE_OF_A_ZONE_CONCESSION)
        .swissLineNumber("r.01")
        .description("Linie 1")
        .number("1")
        .offerCategory(OfferCategory.B)
        .businessOrganisation("ch:1:sboid:10000011")
        .shortNumber("1")
        .build();
    insertLineVersion(line);

    Line oldLine = Line.builder()
        .id(1001L)
        .slnid(MAINLINE_SLNID)
        .validFrom(LocalDate.now().minusDays(5))
        .validTo(LocalDate.now().minusDays(1))
        .status(Status.VALIDATED)
        .lineType(LineType.ORDERLY)
        .concessionType(LineConcessionType.LINE_OF_A_ZONE_CONCESSION)
        .swissLineNumber("r.01")
        .description("Old Linie 1")
        .number("1")
        .offerCategory(OfferCategory.B)
        .businessOrganisation("ch:1:sboid:10000011")
        .build();
    insertLineVersion(oldLine);
  }

  @AfterEach
  void tearDown() throws SQLException {
    cleanupLines();
  }

  @Test
  void shouldAddMainlineAttributesToSubline() {
    Subline subline = Subline.builder()
        .mainlineSlnid(MAINLINE_SLNID)
        .build();

    Subline enrichedSubline = mainlineEnrichingProcessor.addMainlinePropertiesToSubline(subline);
    assertThat(enrichedSubline.getNumber()).isEqualTo("1");
    assertThat(enrichedSubline.getSwissLineNumber()).isEqualTo("r.01");
    assertThat(enrichedSubline.getOfferCategory()).isEqualTo(OfferCategory.B);
    assertThat(enrichedSubline.getShortNumber()).isEqualTo("1");
  }
}