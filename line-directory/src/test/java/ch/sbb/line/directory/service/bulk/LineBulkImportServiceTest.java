package ch.sbb.line.directory.service.bulk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.LineUpdateCsvModel;
import ch.sbb.atlas.imports.model.LineUpdateCsvModel.Fields;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.user.administration.security.service.BusinessOrganisationBasedUserAdministrationService;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.repository.LineVersionRepository;
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
  void shouldUpdateAndGetMoreVersions() {
    assertThat(lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersion.getSlnid())).hasSize(1);

    lineBulkImportService.updateLine(BulkImportUpdateContainer.<LineUpdateCsvModel>builder()
        .object(LineUpdateCsvModel.builder()
            .slnid(lineVersion.getSlnid())
            .validFrom(LocalDate.of(2020, 4, 1))
            .validTo(LocalDate.of(2020, 7, 31))
            .longName("LongName")
            .build())
        .build());

    List<LineVersion> versions =
        lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersion.getSlnid());
    assertThat(versions).hasSize(3);

    LineVersion firstVersion = versions.getFirst();
    assertThat(firstVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstVersion.getValidTo()).isEqualTo(LocalDate.of(2020, 3, 31));
    assertThat(firstVersion.getLongName()).isNull();

    LineVersion secondVersion = versions.get(1);
    assertThat(secondVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 4, 1));
    assertThat(secondVersion.getValidTo()).isEqualTo(LocalDate.of(2020, 7, 31));
    assertThat(secondVersion.getLongName()).isEqualTo("LongName");

    LineVersion thirdVersion = versions.getLast();
    assertThat(thirdVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 8, 1));
    assertThat(thirdVersion.getValidTo()).isEqualTo(LocalDate.of(2020, 12, 31));
    assertThat(thirdVersion.getLongName()).isNull();
  }

}