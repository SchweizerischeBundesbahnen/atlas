package ch.sbb.line.directory.module.ttfn.service.quovadis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.lidi.enumaration.TtfnMeanOfTransport;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.module.ttfn.repository.TimetableFieldNumberVersionRepository;
import ch.sbb.line.directory.module.ttfn.service.quovadis.QuoVadisDataMapper.TimetableFieldNumberV2;
import ch.sbb.line.directory.shared.businessorganisation.entity.SharedBusinessOrganisationVersion;
import ch.sbb.line.directory.shared.businessorganisation.repository.SharedBusinessOrganisationVersionRepository;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class QuoVadisDataImportServiceTest {

  @MockitoBean
  private SharedBusinessOrganisationVersionRepository sharedBusinessOrganisationVersionRepository;

  @MockitoBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @Autowired
  private QuoVadisDataImportService quoVadisDataImportService;

  @Autowired
  private TimetableFieldNumberVersionRepository timetableFieldNumberVersionRepository;

  @BeforeEach
  void setUp() {
    List<SharedBusinessOrganisationVersion> businessOrganisation = List.of(SharedBusinessOrganisationVersion.builder()
        .sboid("ch:1:sboid:123123")
        .build());
    when(sharedBusinessOrganisationVersionRepository.findByOrganisationNumber(any())).thenReturn(businessOrganisation);
    when(sharedBusinessOrganisationService.existsBySboid(any())).thenReturn(true);
  }

  @Test
  void shouldImportDataFromQuoVadisUpdatingTtfn() {
    String number = "80.099.1";
    String ttfnid = "ch:1:ttfnid:123";

    timetableFieldNumberVersionRepository.save(TimetableFieldNumberVersion.builder()
        .ttfnid(ttfnid)
        .descriptionOutwardLine1("Bern - Wohlen")
        .number(number)
        .status(Status.VALIDATED)
        .swissTimetableFieldNumber("r.80.077.1")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .businessOrganisation("ch:1:sboid:123123")
        .build());

    List<TimetableFieldNumberV2> data = List.of(TimetableFieldNumberV2.builder()
        .number(number)
        .meanOfTransport(TtfnMeanOfTransport.BUS)
        .descriptionOutwardLine1("Bern - Wohlen")
        .descriptionOutwardLine2("Line 1")
        .descriptionOutwardLine3("Wichtige")
        .descriptionReturnLine1("Wohlen - Bern")
        .descriptionReturnLine2("Line 1")
        .descriptionReturnLine3("Wichtige")
        .businessOrganisationNumber(11)
        .build());

    // when
    quoVadisDataImportService.performDataMigration(data);

    // then
    List<TimetableFieldNumberVersion> versionsAfterMigration = timetableFieldNumberVersionRepository.getAllVersionsVersioned(
        ttfnid);
    assertThat(versionsAfterMigration).hasSize(2);

    TimetableFieldNumberVersion firstVersion = versionsAfterMigration.getFirst();
    assertThat(firstVersion.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstVersion.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 13));

    TimetableFieldNumberVersion secondVersion = versionsAfterMigration.getLast();
    assertThat(secondVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 12, 14));
    assertThat(secondVersion.getValidTo()).isEqualTo(LocalDate.of(9999, 12, 31));
  }

  @Test
  void shouldImportDataFromQuoVadisCreatingTtfn() {
    List<TimetableFieldNumberV2> data = List.of(TimetableFieldNumberV2.builder()
        .number("80.099.1")
        .meanOfTransport(TtfnMeanOfTransport.BUS)
        .descriptionOutwardLine1("Bern - Wohlen")
        .descriptionOutwardLine2("Line 1")
        .descriptionOutwardLine3("Wichtige")
        .descriptionReturnLine1("Wohlen - Bern")
        .descriptionReturnLine2("Line 1")
        .descriptionReturnLine3("Wichtige")
        .businessOrganisationNumber(11)
        .build());

    // when
    quoVadisDataImportService.performDataMigration(data);

    // then
    List<TimetableFieldNumberVersion> versionsAfterMigration = timetableFieldNumberVersionRepository.findAll();
    assertThat(versionsAfterMigration).hasSize(1);
    assertThat(versionsAfterMigration.getFirst().getTtfnid()).isNotNull();
  }

}