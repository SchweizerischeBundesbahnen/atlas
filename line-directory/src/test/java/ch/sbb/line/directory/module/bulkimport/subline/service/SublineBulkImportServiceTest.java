package ch.sbb.line.directory.module.bulkimport.subline.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import ch.sbb.atlas.api.lidi.enumaration.SublineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.SublineUpdateCsvModel;
import ch.sbb.atlas.imports.model.SublineUpdateCsvModel.Fields;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.user.administration.security.service.BusinessOrganisationBasedUserAdministrationService;
import ch.sbb.line.directory.module.line.LineTestData;
import ch.sbb.line.directory.module.line.repository.LineVersionRepository;
import ch.sbb.line.directory.module.subline.SublineTestData;
import ch.sbb.line.directory.module.subline.entity.SublineVersion;
import ch.sbb.line.directory.module.subline.repository.SublineVersionRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@IntegrationTest
class SublineBulkImportServiceTest {

  private static final String[] IGNORE_FIELDS = new String[]{"version", "editionDate", "creationDate", "editor", "creator"};

  @MockitoBean
  private BusinessOrganisationBasedUserAdministrationService userAdministrationService;

  @MockitoBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @Autowired
  private LineVersionRepository lineVersionRepository;

  @Autowired
  private SublineVersionRepository sublineVersionRepository;

  @Autowired
  private SublineBulkImportService sublineBulkImportService;

  private SublineVersion sublineVersion;

  @BeforeEach
  void setUp() {
    doReturn(true).when(userAdministrationService).hasUserPermissionsToUpdate(any(), any(), any());
    doNothing().when(sharedBusinessOrganisationService).validateSboidExists(any());

    lineVersionRepository.save(LineTestData.lineVersionV2Builder()
        .slnid(SublineTestData.MAINLINE_SLNID)
        .build());
    sublineVersion = sublineVersionRepository.save(SublineTestData.sublineVersionBuilder()
        .mainlineSlnid(SublineTestData.MAINLINE_SLNID)
        .slnid(SublineTestData.MAINLINE_SLNID + ":1")
        .linienId("1a")
        .longName(null)
        .build());
  }

  @AfterEach
  void tearDown() {
    sublineVersionRepository.deleteAll();
    lineVersionRepository.deleteAll();
  }

  @Test
  void shouldUpdateBulkAddingProperty() {
    assertThat(sublineVersion.getLongName()).isNull();

    sublineBulkImportService.updateSubline(BulkImportUpdateContainer.<SublineUpdateCsvModel>builder()
        .object(SublineUpdateCsvModel.builder()
            .slnid(sublineVersion.getSlnid())
            .validFrom(sublineVersion.getValidFrom())
            .validTo(sublineVersion.getValidTo())
            .longName("LongName")
            .build())
        .build());
    SublineVersion version =
        sublineVersionRepository.findById(sublineVersion.getId()).orElseThrow();
    assertThat(version.getLongName()).isEqualTo("LongName");
  }

  @Test
  void shouldUpdateEveryAttribute() {
    sublineVersion.setSublineType(SublineType.CONCESSION);
    sublineVersion.setConcessionType(SublineConcessionType.CANTONALLY_APPROVED_LINE);
    sublineVersion.setSwissSublineNumber("b0.BEX:a");
    sublineVersion = sublineVersionRepository.save(sublineVersion);

    sublineBulkImportService.updateSubline(BulkImportUpdateContainer.<SublineUpdateCsvModel>builder()
        .object(SublineUpdateCsvModel.builder()
            .slnid(sublineVersion.getSlnid())
            .validFrom(sublineVersion.getValidFrom())
            .validTo(sublineVersion.getValidTo())
            .longName("new")
            .linienId("new")
            .description("new")
            .swissSublineNumber("new")
            .businessOrganisation("new")
            .sublineConcessionType(SublineConcessionType.NOT_LICENSED_UNPUBLISHED_LINE)
            .build())
        .build());

    SublineVersion expected = SublineVersion.builder()
        .id(sublineVersion.getId())
        .slnid(sublineVersion.getSlnid())
        .mainlineSlnid(sublineVersion.getMainlineSlnid())
        .validFrom(sublineVersion.getValidFrom())
        .validTo(sublineVersion.getValidTo())
        .longName("new")
        .linienId("new")
        .description("new")
        .swissSublineNumber("new")
        .businessOrganisation("new")
        .concessionType(SublineConcessionType.NOT_LICENSED_UNPUBLISHED_LINE)
        .sublineType(SublineType.CONCESSION)
        .status(Status.VALIDATED)
        .build();

    SublineVersion version =
        sublineVersionRepository.findById(sublineVersion.getId()).orElseThrow();
    assertThat(version).usingRecursiveComparison().ignoringFields(IGNORE_FIELDS).isEqualTo(expected);
  }

  @Test
  void shouldUpdateBulkWithUserInNameOf() {
    sublineBulkImportService.updateSublineByUsername("e123456",
        BulkImportUpdateContainer.<SublineUpdateCsvModel>builder()
            .object(SublineUpdateCsvModel.builder()
                .slnid(sublineVersion.getSlnid())
                .validFrom(sublineVersion.getValidFrom())
                .validTo(sublineVersion.getValidTo())
                .longName("LongName")
                .build())
            .build());

    SublineVersion lineVersion1 =
        sublineVersionRepository.findById(sublineVersion.getId()).orElseThrow();
    assertThat(lineVersion1.getLongName()).isEqualTo("LongName");
  }

  @Test
  void shouldUpdateBulkRemovingProperty() {
    assertThat(sublineVersion.getLinienId()).isEqualTo("1a");

    sublineBulkImportService.updateSubline(BulkImportUpdateContainer.<SublineUpdateCsvModel>builder()
        .object(SublineUpdateCsvModel.builder()
            .slnid(sublineVersion.getSlnid())
            .validFrom(sublineVersion.getValidFrom())
            .validTo(sublineVersion.getValidTo())
            .build())
        .attributesToNull(List.of(Fields.linienId))
        .build());

    SublineVersion sublineVersion1 =
        sublineVersionRepository.findById(sublineVersion.getId()).orElseThrow();
    assertThat(sublineVersion1.getLinienId()).isNull();
  }

  @Test
  void shouldUpdateAndGetMoreVersions() {
    assertThat(sublineVersionRepository.findAllBySlnidOrderByValidFrom(sublineVersion.getSlnid())).hasSize(1);

    sublineBulkImportService.updateSubline(BulkImportUpdateContainer.<SublineUpdateCsvModel>builder()
        .object(SublineUpdateCsvModel.builder()
            .slnid(sublineVersion.getSlnid())
            .validFrom(LocalDate.of(2020, 4, 1))
            .validTo(LocalDate.of(2020, 7, 31))
            .longName("LongName")
            .build())
        .build());

    List<SublineVersion> versions =
        sublineVersionRepository.findAllBySlnidOrderByValidFrom(sublineVersion.getSlnid());
    assertThat(versions).hasSize(3);

    SublineVersion firstVersion = versions.getFirst();
    assertThat(firstVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstVersion.getValidTo()).isEqualTo(LocalDate.of(2020, 3, 31));
    assertThat(firstVersion.getLongName()).isNull();

    SublineVersion secondVersion = versions.get(1);
    assertThat(secondVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 4, 1));
    assertThat(secondVersion.getValidTo()).isEqualTo(LocalDate.of(2020, 7, 31));
    assertThat(secondVersion.getLongName()).isEqualTo("LongName");

    SublineVersion thirdVersion = versions.getLast();
    assertThat(thirdVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 8, 1));
    assertThat(thirdVersion.getValidTo()).isEqualTo(LocalDate.of(2020, 12, 31));
    assertThat(thirdVersion.getLongName()).isNull();
  }
}