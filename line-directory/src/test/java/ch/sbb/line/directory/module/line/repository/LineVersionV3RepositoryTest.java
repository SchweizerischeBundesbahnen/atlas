package ch.sbb.line.directory.module.line.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import ch.sbb.atlas.api.lidi.enumaration.LineMeanOfTransport;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

@IntegrationTest
class LineVersionV3RepositoryTest {

  private final LineVersionV3Repository lineVersionV3Repository;

  @Autowired
  LineVersionV3RepositoryTest(LineVersionV3Repository lineVersionV3Repository) {
    this.lineVersionV3Repository = lineVersionV3Repository;
  }

  @AfterEach
  void tearDown() {
    lineVersionV3Repository.deleteAll();
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

}
