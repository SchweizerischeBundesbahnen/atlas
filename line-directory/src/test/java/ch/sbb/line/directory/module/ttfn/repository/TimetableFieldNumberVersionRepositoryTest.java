package ch.sbb.line.directory.module.ttfn.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.enumaration.TtfnMeanOfTransport;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldNumberVersion;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class TimetableFieldNumberVersionRepositoryTest {

  private final TimetableFieldNumberVersionRepository versionRepository;
  private TimetableFieldNumberVersion version;

  @Autowired
  TimetableFieldNumberVersionRepositoryTest(TimetableFieldNumberVersionRepository versionRepository) {
    this.versionRepository = versionRepository;
  }

  @BeforeEach
  void setUp() {
    version = TimetableFieldNumberVersion.builder()
        .ttfnid("ch:1:ttfnid:100000")
        .descriptionOutwardLine1("FPFN Description")
        .descriptionReturnLine1("FPFN Description")
        .meanOfTransport(TtfnMeanOfTransport.TRAIN)
        .number("80.099")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2020, 12, 12))
        .validTo(LocalDate.of(2020, 12, 12))
        .businessOrganisation("sbb")
        .build();
    version = versionRepository.save(version);

    assertThat(version.getCreator()).isEqualTo(WithMockJwtAuthentication.MOCKUSER_SBB_UID);
    assertThat(version.getEditor()).isEqualTo(WithMockJwtAuthentication.MOCKUSER_SBB_UID);
  }

  @Test
  void shouldGetSimpleVersion() {
    //given
    //when
    TimetableFieldNumberVersion result = versionRepository.findAll().getFirst();

    //then
    assertThat(result).usingRecursiveComparison().ignoringActualNullFields().isEqualTo(version);
  }

  @Test
  void shouldGetCountVersions() {
    //when
    long result = versionRepository.count();

    //then
    assertThat(result).isEqualTo(1);
  }

  @Test
  void shouldUpdateVersionOnAllVersions() {
    //given
    assertThat(version.getVersion()).isZero();

    //when
    versionRepository.incrementVersion(version.getTtfnid());
    TimetableFieldNumberVersion result = versionRepository.findAll().getFirst();

    //then
    assertThat(result.getVersion()).isEqualTo(1);
  }

  @Test
  void shouldDeleteVersion() {
    //given
    versionRepository.delete(version);

    //when
    List<TimetableFieldNumberVersion> result = versionRepository.findAll();

    //then
    assertThat(result).isEmpty();
  }

  @Test
  void shouldDeleteVersions() {
    //given
    String ttfnid = "ch:1:ttfnid:100000";
    TimetableFieldNumberVersion secondVersion = TimetableFieldNumberVersion.builder()
        .ttfnid("ch:1:ttfnid:100000")
        .descriptionOutwardLine1("FPFN Description2")
        .descriptionReturnLine1("FPFN Description2")
        .meanOfTransport(TtfnMeanOfTransport.TRAIN)
        .number("80.099.2")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2021, 12, 12))
        .validTo(LocalDate.of(2021, 12, 12))
        .businessOrganisation("sbb")
        .build();
    versionRepository.save(secondVersion);

    List<TimetableFieldNumberVersion> allVersionsVersioned = versionRepository.findBySid4ptOrderByValidFrom(ttfnid);
    assertThat(allVersionsVersioned).hasSize(2);

    //when
    versionRepository.deleteAll(allVersionsVersioned);

    //then
    List<TimetableFieldNumberVersion> result = versionRepository.findBySid4ptOrderByValidFrom(ttfnid);
    assertThat(result).isEmpty();
  }
}