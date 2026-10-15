package ch.sbb.line.directory.module.line.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import ch.sbb.atlas.api.lidi.enumaration.v3.LineMeanOfTransport;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.repository.LineVersionV3Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class LineVersionV3VersioningTest {

  private static final String SLNID = LineVersionV3TestData.PARENT_SLNID;
  private static final String DESCRIPTION = "Val-Surses-Bus";
  private static final LocalDate VALID_FROM = LocalDate.of(2020, 1, 1);
  private static final LocalDate VALID_TO = LocalDate.of(2020, 12, 31);

  private final LineVersionV3Repository lineVersionV3Repository;
  private final VersionableService versionableService;

  @Autowired
  LineVersionV3VersioningTest(LineVersionV3Repository lineVersionV3Repository, VersionableService versionableService) {
    this.lineVersionV3Repository = lineVersionV3Repository;
    this.versionableService = versionableService;
  }

  @AfterEach
  void cleanUp() {
    lineVersionV3Repository.deleteAll();
  }

  /**
   * NEU:            |______|
   * IST:      |----------------------|
   * RESULTAT: |-----|______|---------|
   */
  @Test
  void shouldSplitVersionWhenDescriptionIsUpdatedInTheMiddle() {
    // Given
    LineVersionV3 current = lineVersionV3Repository.saveAndFlush(existingVersion());
    LineVersionV3 edited = editedVersion(current)
        .description("Description <changed>")
        .validFrom(LocalDate.of(2020, 6, 1))
        .validTo(LocalDate.of(2020, 6, 30))
        .build();

    // When
    List<LineVersionV3> result = update(current, edited);

    // Then
    assertThat(result)
        .extracting(LineVersionV3::getValidFrom, LineVersionV3::getValidTo, LineVersionV3::getDescription)
        .containsExactly(
            tuple(VALID_FROM, LocalDate.of(2020, 5, 31), DESCRIPTION),
            tuple(LocalDate.of(2020, 6, 1), LocalDate.of(2020, 6, 30), "Description <changed>"),
            tuple(LocalDate.of(2020, 7, 1), VALID_TO, DESCRIPTION));
  }

  @Test
  void shouldKeepSlnidAndUnchangedPropertiesOnAllSplitVersions() {
    // Given
    LineVersionV3 current = lineVersionV3Repository.saveAndFlush(existingVersion());
    LineVersionV3 edited = editedVersion(current)
        .description("Description <changed>")
        .validFrom(LocalDate.of(2020, 6, 1))
        .validTo(LocalDate.of(2020, 6, 30))
        .build();

    // When
    List<LineVersionV3> result = update(current, edited);

    // Then
    assertThat(result)
        .extracting(LineVersionV3::getSlnid, LineVersionV3::getSwissLineNumber, LineVersionV3::getNumber,
            LineVersionV3::getMeansOfTransport)
        .containsOnly(tuple(SLNID, "90.583", "90.583", Set.of(LineMeanOfTransport.TRAIN)));
  }

  @Test
  void shouldSplitVersionWhenMeansOfTransportIsUpdatedInTheMiddle() {
    // Given
    LineVersionV3 current = lineVersionV3Repository.saveAndFlush(existingVersion());
    LineVersionV3 edited = editedVersion(current)
        .meansOfTransport(Set.of(LineMeanOfTransport.TRAIN, LineMeanOfTransport.BUS))
        .validFrom(LocalDate.of(2020, 6, 1))
        .validTo(LocalDate.of(2020, 6, 30))
        .build();

    // When
    List<LineVersionV3> result = update(current, edited);

    // Then
    assertThat(result)
        .extracting(LineVersionV3::getValidFrom, LineVersionV3::getValidTo, LineVersionV3::getMeansOfTransport)
        .containsExactly(
            tuple(VALID_FROM, LocalDate.of(2020, 5, 31), Set.of(LineMeanOfTransport.TRAIN)),
            tuple(LocalDate.of(2020, 6, 1), LocalDate.of(2020, 6, 30),
                Set.of(LineMeanOfTransport.TRAIN, LineMeanOfTransport.BUS)),
            tuple(LocalDate.of(2020, 7, 1), VALID_TO, Set.of(LineMeanOfTransport.TRAIN)));
  }

  @Test
  void shouldNotSplitVersionWhenMeansOfTransportIsUnchanged() {
    // Given
    LineVersionV3 current = lineVersionV3Repository.saveAndFlush(existingVersion());
    LineVersionV3 edited = editedVersion(current)
        .comment("only the comment changed")
        .build();

    // When
    List<LineVersionV3> result = update(current, edited);

    // Then
    assertThat(result)
        .extracting(LineVersionV3::getValidFrom, LineVersionV3::getValidTo, LineVersionV3::getMeansOfTransport,
            LineVersionV3::getComment)
        .containsExactly(tuple(VALID_FROM, VALID_TO, Set.of(LineMeanOfTransport.TRAIN), "only the comment changed"));
  }

  @Test
  void shouldDeleteOptionalPropertyWhenItIsSetToNull() {
    // Given
    LineVersionV3 current = lineVersionV3Repository.saveAndFlush(existingVersion());
    LineVersionV3 edited = editedVersion(current)
        .comment(null)
        .build();

    // When
    List<LineVersionV3> result = update(current, edited);

    // Then
    assertThat(result)
        .extracting(LineVersionV3::getValidFrom, LineVersionV3::getValidTo, LineVersionV3::getComment,
            LineVersionV3::getDescription)
        .containsExactly(tuple(VALID_FROM, VALID_TO, null, DESCRIPTION));
  }

  private static LineVersionV3 existingVersion() {
    return LineVersionV3TestData.lineBuilder()
        .slnid(SLNID)
        .comment("comment")
        .validFrom(VALID_FROM)
        .validTo(VALID_TO)
        .build();
  }

  private static LineVersionV3.LineVersionV3Builder<?, ?> editedVersion(LineVersionV3 current) {
    return existingVersion().toBuilder()
        .id(null)
        .status(null)
        .version(current.getVersion());
  }

  private List<LineVersionV3> update(LineVersionV3 current, LineVersionV3 edited) {
    List<LineVersionV3> currentVersions = lineVersionV3Repository.findAllBySlnidOrderByValidFrom(current.getSlnid());
    List<VersionedObject> versionedObjects = versionableService.versioningObjectsDeletingNullProperties(current, edited,
        currentVersions);
    versionableService.applyVersioning(LineVersionV3.class, versionedObjects, this::save,
        lineVersionV3Repository::deleteById);
    return lineVersionV3Repository.findAllBySlnidOrderByValidFrom(current.getSlnid());
  }

  private void save(LineVersionV3 version) {
    version.setStatus(Status.VALIDATED);
    lineVersionV3Repository.saveAndFlush(version);
  }

}
