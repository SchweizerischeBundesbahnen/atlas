package ch.sbb.line.directory.module.bulkimport.line.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.LineCreateCsvModel;
import ch.sbb.atlas.imports.model.LineUpdateCsvModel;
import ch.sbb.atlas.imports.model.LineUpdateCsvModel.Fields;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.user.administration.security.service.BusinessOrganisationBasedUserAdministrationService;
import ch.sbb.line.directory.module.line.LineTestData;
import ch.sbb.line.directory.module.line.entity.LineVersion;
import ch.sbb.line.directory.module.line.repository.LineVersionRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@IntegrationTest
class LineBulkImportServiceTest {

  @MockitoBean
  private BusinessOrganisationBasedUserAdministrationService userAdministrationService;

  @MockitoBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @Autowired
  private LineVersionRepository lineVersionRepository;

  @Autowired
  private LineBulkImportService lineBulkImportService;

  private LineVersion lineVersion;

  @BeforeEach
  void setUp() {
    doReturn(true).when(userAdministrationService).hasUserPermissionsToUpdate(any(), any(), any());
    doReturn(true).when(userAdministrationService).hasUserPermissionsToCreate(any(), any());
    doNothing().when(sharedBusinessOrganisationService).validateSboidExists(any());
    lineVersion = lineVersionRepository.save(LineTestData.lineVersionV2Builder().longName(null).build());
  }

  @AfterEach
  void tearDown() {
    lineVersionRepository.deleteAll();
  }

  @Test
  void shouldUpdateBulkAddingProperty() {
    assertThat(lineVersion.getLongName()).isNull();

    lineBulkImportService.updateLine(BulkImportUpdateContainer.<LineUpdateCsvModel>builder()
        .object(LineUpdateCsvModel.builder()
            .slnid(lineVersion.getSlnid())
            .validFrom(lineVersion.getValidFrom())
            .validTo(lineVersion.getValidTo())
            .longName("LongName")
            .build())
        .build());
    LineVersion version =
        lineVersionRepository.findById(lineVersion.getId()).orElseThrow();
    assertThat(version.getLongName()).isEqualTo("LongName");
  }

  @Test
  void shouldUpdateBulkWithUserInNameOf() {
    lineBulkImportService.updateLineByUsername("e123456",
        BulkImportUpdateContainer.<LineUpdateCsvModel>builder()
            .object(LineUpdateCsvModel.builder()
                .slnid(lineVersion.getSlnid())
                .validFrom(lineVersion.getValidFrom())
                .validTo(lineVersion.getValidTo())
                .longName("LongName")
                .build())
            .build());

    LineVersion lineVersion1 =
        lineVersionRepository.findById(lineVersion.getId()).orElseThrow();
    assertThat(lineVersion1.getLongName()).isEqualTo("LongName");
  }

  @Test
  void shouldUpdateBulkRemovingProperty() {
    assertThat(lineVersion.getShortNumber()).isEqualTo("6");

    lineBulkImportService.updateLine(BulkImportUpdateContainer.<LineUpdateCsvModel>builder()
        .object(LineUpdateCsvModel.builder()
            .slnid(lineVersion.getSlnid())
            .validFrom(lineVersion.getValidFrom())
            .validTo(lineVersion.getValidTo())
            .build())
        .attributesToNull(List.of(Fields.shortNumber))
        .build());

    LineVersion lineVersion1 =
        lineVersionRepository.findById(lineVersion.getId()).orElseThrow();
    assertThat(lineVersion1.getShortNumber()).isNull();
  }

  @Test
  void shouldCreateLine() {
    lineBulkImportService.createLine(BulkImportUpdateContainer.<LineCreateCsvModel>builder()
        .object(LineCreateCsvModel.builder()
            .linienId("320")
            .validFrom(LocalDate.of(2021, 4, 1))
            .validTo(LocalDate.of(2099, 12, 31))
            .description("Chur - Thusis - St. Moritz - Pontresina - Tirano")
            .number("BEX1")
            .swissLineNumber("b0.BEX9")
            .lineType(LineType.ORDERLY)
            .lineConcessionType(LineConcessionType.FEDERALLY_LICENSED_OR_APPROVED_LINE)
            .offerCategory(OfferCategory.IR)
            .shortNumber("EX")
            .longName("Bernina Express")
            .businessOrganisation("ch:1:sboid:100053")
            .comment("Bernina Express / Konzessionsrecht ist nur für den schweizerischen Linienabschnitt gültig")
            .build())
        .build());

    LineVersion lineVersion1 = LineVersion.builder()
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .swissLineNumber("b0.BEX9")
        .build();

    LineVersion lineVersion2 = lineVersionRepository.findSwissLineNumberOverlaps(
        lineVersion1).getFirst();
    assertThat(lineVersion2.getSwissLineNumber()).isNotNull().isEqualTo("b0.BEX9");
  }

}
