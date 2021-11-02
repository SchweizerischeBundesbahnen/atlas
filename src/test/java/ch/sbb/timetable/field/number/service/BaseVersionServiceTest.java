package ch.sbb.timetable.field.number.service;

import ch.sbb.timetable.field.number.IntegrationTest;
import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.repository.VersionRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
public abstract class BaseVersionServiceTest {

  protected static final String TTFNID = "ch:1:fpfnid:100000";
  protected VersionRepository versionRepository;
  protected VersionService versionService;

  protected Version version1;
  protected Version version2;
  protected Version version3;

  @Autowired
  public BaseVersionServiceTest(VersionRepository versionRepository,
      VersionService versionService) {
    this.versionRepository = versionRepository;
    this.versionService = versionService;
  }

  @BeforeEach
  void init() {
    version1 = Version.builder().ttfnid(TTFNID)
                      .name("FPFN Name")
                      .number("BEX")
                      .swissTimetableFieldNumber("b0.BEX")
                      .validFrom(LocalDate.of(2020, 1, 1))
                      .validTo(LocalDate.of(2021, 12, 31))
                      .build();
    version2 = Version.builder().ttfnid(TTFNID)
                      .name("FPFN Name")
                      .number("BEX")
                      .swissTimetableFieldNumber("b0.BEX")
                      .validFrom(LocalDate.of(2022, 1, 1))
                      .validTo(LocalDate.of(2023, 12, 31))
                      .build();
    version3 = Version.builder().ttfnid(TTFNID)
                      .name("FPFN Name")
                      .number("BEX")
                      .swissTimetableFieldNumber("b0.BEX")
                      .validFrom(LocalDate.of(2024, 1, 1))
                      .validTo(LocalDate.of(2024, 12, 31))
                      .build();
  }

  @AfterEach
  void cleanUp() {
    List<Version> versionsVersioned = versionRepository.getAllVersionsVersioned(TTFNID);
    versionRepository.deleteAll(versionsVersioned);
  }

}
