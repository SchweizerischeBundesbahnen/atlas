package ch.sbb.line.directory.module.line.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import ch.sbb.atlas.api.lidi.enumaration.v3.LineMeanOfTransport;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.line.directory.module.line.LineTestData;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersion;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

@IntegrationTest
class LineVersionV3RepositoryTest {

  private static final String SEED_DATA = "/db/testdata/line_version_v3_testdata.sql";
  private static final String SLNID_PREFIX = "ch:1:slnid:";
  private static final String SEEDED_ORDERLY_SLNID = "ch:1:slnid:9000070";
  private static final String SEEDED_PARENT_SLNID = "ch:1:slnid:9000090";
  private static final String SEEDED_CONCESSION_MEMBER_SLNID = "ch:1:slnid:9000091";

  private final LineVersionV3Repository lineVersionV3Repository;
  private final LineVersionRepository lineVersionRepository;
  private LineVersion savedV2LineVersion;

  @Autowired
  LineVersionV3RepositoryTest(LineVersionV3Repository lineVersionV3Repository, LineVersionRepository lineVersionRepository) {
    this.lineVersionV3Repository = lineVersionV3Repository;
    this.lineVersionRepository = lineVersionRepository;
  }

  @AfterEach
  void tearDown() {
    lineVersionV3Repository.deleteAll();
    if (savedV2LineVersion != null) {
      lineVersionRepository.deleteById(savedV2LineVersion.getId());
      savedV2LineVersion = null;
    }
  }

  @Test
  void shouldPersistLineVersionWithAuditingAndGeneratedId() {
    //when
    LineVersionV3 saved = lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());

    //then
    assertThat(saved.getId()).isNotNull().isGreaterThanOrEqualTo(1000L);
    assertThat(saved.getCreationDate()).isNotNull();
    assertThat(saved.getEditionDate()).isNotNull();
    assertThat(saved.getCreator()).isEqualTo(WithMockJwtAuthentication.MOCKUSER_SBB_UID);
    assertThat(saved.getEditor()).isEqualTo(WithMockJwtAuthentication.MOCKUSER_SBB_UID);
  }

  @Test
  void shouldPersistMeansOfTransportInChildTable() {
    //given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder()
        .meansOfTransport(Set.of(LineMeanOfTransport.BUS, LineMeanOfTransport.FERRY))
        .build();

    //when
    lineVersionV3Repository.saveAndFlush(line);
    LineVersionV3 result = lineVersionV3Repository.findAll().getFirst();

    //then
    assertThat(result.getMeansOfTransport()).containsExactlyInAnyOrder(LineMeanOfTransport.BUS, LineMeanOfTransport.FERRY);
  }

  @Test
  void shouldPersistCablewayAttributes() {
    //given
    LineVersionV3 cableway = LineVersionV3TestData.cableway();

    //when
    lineVersionV3Repository.saveAndFlush(cableway);
    LineVersionV3 result = lineVersionV3Repository.findAll().getFirst();

    //then
    assertThat(result.getCablewayNumber()).isEqualTo("3521");
    assertThat(result.getCablewayType()).isEqualTo("Pendelbahn");
    assertThat(result.getCablewayName()).isEqualTo("Luftseilbahn Wengen - Maennlichen");
  }

  @Test
  void shouldRejectDuplicateSlnidAndValidFrom() {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder()
        .validFrom(LocalDate.of(2026, 1, 1))
        .build());
    LineVersionV3 duplicate = LineVersionV3TestData.lineBuilder()
        .validFrom(LocalDate.of(2026, 1, 1))
        .description("other description")
        .build();

    //when && then
    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> lineVersionV3Repository.saveAndFlush(duplicate));
  }

  @Test
  void shouldAllowSameSlnidWithDifferentValidFrom() {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder()
        .validFrom(LocalDate.of(2026, 1, 1))
        .validTo(LocalDate.of(2026, 5, 6))
        .build());

    //when
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder()
        .validFrom(LocalDate.of(2026, 5, 7))
        .build());

    //then
    assertThat(lineVersionV3Repository.findAll()).hasSize(2);
  }

  @Test
  void shouldFindAllBySlnidOrderedByValidFrom() {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder()
        .validFrom(LocalDate.of(2026, 5, 7))
        .build());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder()
        .validFrom(LocalDate.of(2026, 1, 1))
        .validTo(LocalDate.of(2026, 5, 6))
        .build());

    //when
    List<LineVersionV3> result = lineVersionV3Repository.findAllBySlnidOrderByValidFrom(LineVersionV3TestData.PARENT_SLNID);

    //then
    assertThat(result).hasSize(2);
    assertThat(result.getFirst().getValidFrom()).isEqualTo(LocalDate.of(2026, 1, 1));
    assertThat(result.getLast().getValidFrom()).isEqualTo(LocalDate.of(2026, 5, 7));
  }

  @Test
  void shouldFindAllBySlnidIn() {
    //given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.subline());

    //when
    List<LineVersionV3> result = lineVersionV3Repository.findAllBySlnidIn(
        Set.of(LineVersionV3TestData.PARENT_SLNID, LineVersionV3TestData.UNKNOWN_SLNID));

    //then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getSlnid()).isEqualTo(LineVersionV3TestData.PARENT_SLNID);
  }

  @Test
  void shouldGenerateSlnidFromSharedSequenceForLinesAndSublinesWithoutCollidingWithV2() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder().slnid(null).build();
    LineVersion v2Line = LineTestData.lineVersionBuilder().slnid(null).build();

    // When
    LineVersionV3 savedLine = lineVersionV3Repository.saveAndFlush(line);
    savedV2LineVersion = lineVersionRepository.saveAndFlush(v2Line);
    LineVersionV3 savedSubline = lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.sublineBuilder()
        .slnid(null)
        .referencedLineSlnid(savedLine.getSlnid())
        .build());

    // Then
    long lineNumber = slnidNumber(savedLine.getSlnid());
    assertThat(savedLine.getSlnid()).matches("ch:1:slnid:\\d+");
    assertThat(savedV2LineVersion.getSlnid()).isEqualTo(SLNID_PREFIX + (lineNumber + 1));
    assertThat(savedSubline.getSlnid()).isEqualTo(SLNID_PREFIX + (lineNumber + 2));
    assertThat(savedSubline.getReferencedLineSlnid()).isEqualTo(savedLine.getSlnid());
  }

  @Test
  void shouldKeepPresetSlnid() {
    // Given
    LineVersionV3 line = LineVersionV3TestData.lineBuilder().slnid(LineVersionV3TestData.PARENT_SLNID).build();

    // When
    LineVersionV3 saved = lineVersionV3Repository.saveAndFlush(line);

    // Then
    assertThat(saved.getSlnid()).isEqualTo(LineVersionV3TestData.PARENT_SLNID);
    assertThat(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(LineVersionV3TestData.PARENT_SLNID)).hasSize(1);
  }

  @Test
  @Sql(scripts = SEED_DATA)
  void shouldFindSwissLineNumberOverlapsOfOrderlyLine() {
    // Given
    LineVersionV3 newLine = LineVersionV3TestData.lineBuilder()
        .slnid(null)
        .swissLineNumber("b1.SB1")
        .validFrom(LocalDate.of(2030, 1, 1))
        .validTo(LocalDate.of(2030, 12, 31))
        .build();

    // When
    List<LineVersionV3> result = lineVersionV3Repository.findSwissLineNumberOverlaps(newLine);

    // Then
    assertThat(result).extracting(LineVersionV3::getId).containsExactly(900070L);
  }

  @Test
  @Sql(scripts = SEED_DATA)
  void shouldFindSwissLineNumberOverlapsIgnoringCase() {
    // Given
    LineVersionV3 newLine = LineVersionV3TestData.lineBuilder()
        .slnid(null)
        .swissLineNumber("B1.sb1")
        .validFrom(LocalDate.of(2030, 1, 1))
        .validTo(LocalDate.of(2030, 12, 31))
        .build();

    // When
    List<LineVersionV3> result = lineVersionV3Repository.findSwissLineNumberOverlaps(newLine);

    // Then
    assertThat(result).extracting(LineVersionV3::getSlnid).containsExactly(SEEDED_ORDERLY_SLNID);
  }

  @Test
  @Sql(scripts = SEED_DATA)
  void shouldFindSwissLineNumberOverlapsOfConcessionMemberOverAllOverlappingVersions() {
    // Given
    LineVersionV3 newMember = LineVersionV3TestData.concessionMemberBuilder()
        .slnid(null)
        .referencedLineSlnid(SEEDED_PARENT_SLNID)
        .swissLineNumber("b1.K1")
        .validFrom(LocalDate.of(2024, 6, 1))
        .validTo(LocalDate.of(2025, 6, 30))
        .build();

    // When
    List<LineVersionV3> result = lineVersionV3Repository.findSwissLineNumberOverlaps(newMember);

    // Then
    assertThat(result).extracting(LineVersionV3::getId).containsExactlyInAnyOrder(900091L, 900092L);
  }

  @Test
  @Sql(scripts = SEED_DATA)
  void shouldNotFindSwissLineNumberOverlapsWhenValidityIsAdjacent() {
    // Given
    LineVersionV3 newMember = LineVersionV3TestData.concessionMemberBuilder()
        .slnid(null)
        .swissLineNumber("b1.K1")
        .validFrom(LocalDate.of(2023, 1, 1))
        .validTo(LocalDate.of(2023, 12, 31))
        .build();

    // When
    List<LineVersionV3> result = lineVersionV3Repository.findSwissLineNumberOverlaps(newMember);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @Sql(scripts = SEED_DATA)
  void shouldNotFindSwissLineNumberOverlapsForOtherSwissLineNumber() {
    // Given
    LineVersionV3 newMember = LineVersionV3TestData.concessionMemberBuilder()
        .slnid(null)
        .swissLineNumber("b1.K2")
        .validFrom(LocalDate.of(2024, 6, 1))
        .validTo(LocalDate.of(2024, 6, 30))
        .build();

    // When
    List<LineVersionV3> result = lineVersionV3Repository.findSwissLineNumberOverlaps(newMember);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @Sql(scripts = SEED_DATA)
  void shouldNotFindSwissLineNumberOverlapsWithItsOwnSlnid() {
    // Given
    LineVersionV3 editedMember = LineVersionV3TestData.concessionMemberBuilder()
        .slnid(SEEDED_CONCESSION_MEMBER_SLNID)
        .swissLineNumber("b1.K1")
        .validFrom(LocalDate.of(2024, 6, 1))
        .validTo(LocalDate.of(2024, 6, 30))
        .build();

    // When
    List<LineVersionV3> result = lineVersionV3Repository.findSwissLineNumberOverlaps(editedMember);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @Sql(scripts = SEED_DATA)
  void shouldNotFindSwissLineNumberOverlapsWithRevokedLine() {
    // Given
    LineVersionV3 newLine = LineVersionV3TestData.lineBuilder()
        .slnid(null)
        .swissLineNumber("90.583")
        .validFrom(LocalDate.of(2024, 1, 1))
        .validTo(LocalDate.of(2024, 12, 31))
        .build();

    // When
    List<LineVersionV3> result = lineVersionV3Repository.findSwissLineNumberOverlaps(newLine);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @Sql(scripts = SEED_DATA)
  void shouldNotFindSwissLineNumberOverlapsWhenSwissLineNumberIsNull() {
    // Given
    LineVersionV3 technicalMember = LineVersionV3TestData.sublineBuilder()
        .slnid(null)
        .referencedLineSlnid(SEEDED_PARENT_SLNID)
        .swissLineNumber(null)
        .validFrom(LocalDate.of(2024, 1, 1))
        .validTo(LocalDate.of(2024, 12, 31))
        .build();

    // When
    List<LineVersionV3> result = lineVersionV3Repository.findSwissLineNumberOverlaps(technicalMember);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @Sql(scripts = SEED_DATA)
  void shouldFindAllMembersOfParentOrderedByValidFrom() {
    // When
    List<LineVersionV3> result = lineVersionV3Repository.findAllByReferencedLineSlnid(SEEDED_PARENT_SLNID);

    // Then
    assertThat(result).extracting(LineVersionV3::getId).containsExactly(900093L, 900091L, 900092L);
  }

  @Test
  @Sql(scripts = SEED_DATA)
  void shouldFindNoMembersOfLineWithoutMembers() {
    // When
    List<LineVersionV3> result = lineVersionV3Repository.findAllByReferencedLineSlnid(SEEDED_ORDERLY_SLNID + "-none");

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void shouldIncrementVersionOfAllVersionsOfSlnid() {
    // Given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder()
        .validFrom(LocalDate.of(2026, 1, 1))
        .validTo(LocalDate.of(2026, 5, 6))
        .build());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.lineBuilder()
        .validFrom(LocalDate.of(2026, 5, 7))
        .build());

    // When
    lineVersionV3Repository.incrementVersion(LineVersionV3TestData.PARENT_SLNID);

    // Then
    assertThat(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(LineVersionV3TestData.PARENT_SLNID))
        .extracting(LineVersionV3::getVersion)
        .containsExactly(1, 1);
  }

  @Test
  void shouldNotIncrementVersionOfOtherSlnid() {
    // Given
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());
    lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.cableway());

    // When
    lineVersionV3Repository.incrementVersion(LineVersionV3TestData.PARENT_SLNID);

    // Then
    assertThat(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(LineVersionV3TestData.CABLEWAY_SLNID))
        .extracting(LineVersionV3::getVersion)
        .containsExactly(0);
  }

  private static long slnidNumber(String slnid) {
    return Long.parseLong(slnid.substring(SLNID_PREFIX.length()));
  }

}
