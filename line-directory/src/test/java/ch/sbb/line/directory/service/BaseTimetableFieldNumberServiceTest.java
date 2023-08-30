package ch.sbb.line.directory.service;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion.TimetableFieldNumberVersionBuilder;
import ch.sbb.line.directory.repository.TimetableFieldNumberVersionRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public abstract class BaseTimetableFieldNumberServiceTest {

  @MockBean
  private TimetableFieldNumberValidationService timetableFieldNumberValidationService;

  protected static final String TTFNID = "ch:1:ttfnid:100000";
  protected TimetableFieldNumberVersionRepository versionRepository;
  protected TimetableFieldNumberService timetableFieldNumberService;

  protected TimetableFieldNumberVersion version1;
  protected TimetableFieldNumberVersion version2;
  protected TimetableFieldNumberVersion version3;
  protected TimetableFieldNumberVersion version4;
  protected TimetableFieldNumberVersion version5;

  @Autowired
  public BaseTimetableFieldNumberServiceTest(TimetableFieldNumberVersionRepository versionRepository,
      TimetableFieldNumberService timetableFieldNumberService) {
    this.versionRepository = versionRepository;
    this.timetableFieldNumberService = timetableFieldNumberService;
  }

  @BeforeEach
  void init() {
    version1 = version1Builder().build();
    version2 = version2Builder().build();
    version3 = version3Builder().build();
    version4 = version4Builder().build();
    version5 = version5Builder().build();
  }

  protected static TimetableFieldNumberVersionBuilder<?, ?> version5Builder() {
    return TimetableFieldNumberVersion.builder().ttfnid(TTFNID)
        .description("FPFN Description")
        .number("BEX5")
        .status(Status.VALIDATED)
        .swissTimetableFieldNumber("b0.BEX")
        .validFrom(LocalDate.of(2026, 1, 1))
        .validTo(LocalDate.of(2026, 12, 31))
        .businessOrganisation("sbb");
  }

  protected static TimetableFieldNumberVersionBuilder<?, ?> version4Builder() {
    return TimetableFieldNumberVersion.builder().ttfnid(TTFNID)
        .description("FPFN Description")
        .number("BEX4")
        .status(Status.VALIDATED)
        .swissTimetableFieldNumber("b0.BEX")
        .validFrom(LocalDate.of(2025, 1, 1))
        .validTo(LocalDate.of(2025, 12, 31))
        .businessOrganisation("sbb");
  }

  protected static TimetableFieldNumberVersionBuilder<?, ?> version3Builder() {
    return TimetableFieldNumberVersion.builder().ttfnid(TTFNID)
        .description("FPFN Description")
        .number("BEX3")
        .status(Status.VALIDATED)
        .swissTimetableFieldNumber("b0.BEX")
        .validFrom(LocalDate.of(2024, 1, 1))
        .validTo(LocalDate.of(2024, 12, 31))
        .businessOrganisation("sbb");
  }

  protected static TimetableFieldNumberVersionBuilder<?, ?> version2Builder() {
    return TimetableFieldNumberVersion.builder().ttfnid(TTFNID)
        .description("FPFN Description")
        .number("BEX2")
        .status(Status.VALIDATED)
        .swissTimetableFieldNumber("b0.BEX")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2023, 12, 31))
        .businessOrganisation("sbb");
  }

  protected static TimetableFieldNumberVersionBuilder<?, ?> version1Builder() {
    return TimetableFieldNumberVersion.builder().ttfnid(TTFNID)
        .description("FPFN Description")
        .number("BEX1")
        .status(Status.VALIDATED)
        .swissTimetableFieldNumber("b0.BEX")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .businessOrganisation("sbb");
  }

  @AfterEach
  void cleanUp() {
    List<TimetableFieldNumberVersion> versionsVersioned = versionRepository.getAllVersionsVersioned(TTFNID);
    versionRepository.deleteAll(versionsVersioned);
  }
}
