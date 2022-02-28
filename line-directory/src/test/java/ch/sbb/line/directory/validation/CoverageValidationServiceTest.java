package ch.sbb.line.directory.validation;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.enumaration.SublineType;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import ch.sbb.line.directory.service.CoverageService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CoverageValidationServiceTest {

  @Mock
  private CoverageService coverageService;
  @Mock
  private SublineVersionRepository sublineVersionRepository;
  @Mock
  private LineVersionRepository lineVersionRepository;

  private CoverageValidationService coverageValidationService;

  @BeforeEach()
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    coverageValidationService = new CoverageValidationService(coverageService,
        sublineVersionRepository, lineVersionRepository);
  }

  /**
   * 1a. Case: only line
   *           01.01.2000	         31.12.2000
   * Line	    |-----------------------------|
   * Result OK
   */
  @Test
  public void shouldReturnTrueWhenLineHasNoSublineRelated() {
    //given
    LineVersion lineVersion = LineTestData.lineVersionBuilder().slnid("ch:1000").build();
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(lineVersion);
    doReturn(lineVersions).when(lineVersionRepository)
                              .findAllBySlnidOrderByValidFrom(lineVersion.getSlnid());
    //when
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersion);
    //then
    assertThat(result).isTrue();
  }

  /**
   * 1b. Case: only line
   *           01.01.2000	         31.12.2000
   * Line	    |-----------------------------|
   *           01.01.2000	         31.12.2000
   * Subline  |-----------------------------|
   * Result OK
   */
  @Test
  public void shouldReturnTrueWhenALineCoversCompletelyASubline() {
    //given
    LineVersion lineVersion = LineTestData.lineVersionBuilder()
                                          .validFrom(LocalDate.of(2000, 1, 1))
                                          .validTo(LocalDate.of(2000, 12, 31))
                                          .slnid("ch:1000").build();
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(lineVersion);
    SublineVersion sublineVersion = SublineTestData.sublineVersionBuilder()
                                                   .validFrom(LocalDate.of(2000, 1, 1))
                                                   .validTo(LocalDate.of(2000, 12, 31))
                                                   .mainlineSlnid(lineVersion.getSlnid())
                                                   .slnid("ch:1000")
                                                   .build();
    List<SublineVersion> sublineVersions = new ArrayList<>();
    sublineVersions.add(sublineVersion);
    doReturn(lineVersions).when(lineVersionRepository)
                              .findAllBySlnidOrderByValidFrom(lineVersion.getSlnid());
    doReturn(sublineVersions).when(sublineVersionRepository)
                              .getSublineVersionByMainlineSlnid(lineVersion.getSlnid());
    //when
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersion);
    //then
    assertThat(result).isTrue();
  }

  /**
   * 1c. Case: multiple lines cover multiple sublines without gaps
   * Line	    |-----------------------------|-----------------------------|
   * Subline  |------------------|-----------------|----------------------|
   * Result OK
   */
  @Test
  public void shouldReturnTrueWhenLinesCoverCompletelySublinesWithoutGap() {
    //given
    LineVersion firstLineVersion = LineTestData.lineVersionBuilder()
                                          .validFrom(LocalDate.of(2000, 1, 1))
                                          .validTo(LocalDate.of(2000, 12, 31))
                                          .slnid("ch:1000").build();
    LineVersion secondLineVersion = LineTestData.lineVersionBuilder()
                                          .validFrom(LocalDate.of(2001, 1, 1))
                                          .validTo(LocalDate.of(2001, 12, 31))
                                          .slnid("ch:1000").build();
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(firstLineVersion);
    lineVersions.add(secondLineVersion);
    SublineVersion firstSublineVersion = SublineTestData.sublineVersionBuilder()
                                                   .validFrom(LocalDate.of(2000, 1, 1))
                                                   .validTo(LocalDate.of(2000, 10, 31))
                                                   .mainlineSlnid(firstLineVersion.getSlnid())
                                                   .slnid("ch:1000")
                                                   .build();
    SublineVersion secondSublineVersion = SublineTestData.sublineVersionBuilder()
                                                   .validFrom(LocalDate.of(2000, 11, 1))
                                                   .validTo(LocalDate.of(2001, 7, 31))
                                                   .mainlineSlnid(firstLineVersion.getSlnid())
                                                   .slnid("ch:1000")
                                                   .build();
    SublineVersion thirdSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2001, 8, 1))
                                                         .validTo(LocalDate.of(2001, 12, 31))
                                                         .mainlineSlnid(firstLineVersion.getSlnid())
                                                         .slnid("ch:1000")
                                                         .build();

    List<SublineVersion> sublineVersions = new ArrayList<>();
    sublineVersions.add(firstSublineVersion);
    sublineVersions.add(secondSublineVersion);
    sublineVersions.add(thirdSublineVersion);
    doReturn(lineVersions).when(lineVersionRepository)
                              .findAllBySlnidOrderByValidFrom(firstLineVersion.getSlnid());
    doReturn(sublineVersions).when(sublineVersionRepository)
                              .getSublineVersionByMainlineSlnid(firstLineVersion.getSlnid());
    //when
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(firstLineVersion);
    //then
    assertThat(result).isTrue();
  }

  /**
   * 2. Case: Subline Right Outside
   *           01.01.2000	         31.12.2000
   * Line	    |-----------------------------|
   *           01.01.2000	           31.01.2001
   * Subline  |-------------------------------|
   * Result NOK
   */
  @Test
  public void shouldReturnFalseWhenSublineRangeIsRightOutsideOfLineRanges() {
    //given
    LineVersion lineVersion = LineTestData.lineVersionBuilder()
                                          .validFrom(LocalDate.of(2000, 1, 1))
                                          .validTo(LocalDate.of(2000, 12, 31))
                                          .slnid("ch:1000").build();
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(lineVersion);
    SublineVersion sublineVersion = SublineTestData.sublineVersionBuilder()
                                                   .validFrom(LocalDate.of(2000, 1, 1))
                                                   .validTo(LocalDate.of(2001, 1, 31))
                                                   .mainlineSlnid(lineVersion.getSlnid())
                                                   .slnid("ch:1000")
                                                   .build();
    List<SublineVersion> sublineVersions = new ArrayList<>();
    sublineVersions.add(sublineVersion);
    doReturn(lineVersions).when(lineVersionRepository)
                          .findAllBySlnidOrderByValidFrom(lineVersion.getSlnid());
    doReturn(sublineVersions).when(sublineVersionRepository)
                             .getSublineVersionByMainlineSlnid(lineVersion.getSlnid());
    //when
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersion);
    //then
    assertThat(result).isFalse();
  }

  /**
   * 3. Case: Subline left outside
   *            01.02.2000	         31.12.2000
   * Line	      |-----------------------------|
   *           01.01.2000	           31.12.2000
   * Subline  |-------------------------------|
   * Result NOK
   */
  @Test
  public void shouldReturnFalseWhenSublineRangeIsLeftOutsideOfLineRanges() {
    //given
    LineVersion lineVersion = LineTestData.lineVersionBuilder()
                                          .validFrom(LocalDate.of(2000, 1, 2))
                                          .validTo(LocalDate.of(2000, 12, 31))
                                          .slnid("ch:1000").build();
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(lineVersion);
    SublineVersion sublineVersion = SublineTestData.sublineVersionBuilder()
                                                   .validFrom(LocalDate.of(2000, 1, 1))
                                                   .validTo(LocalDate.of(2000, 12, 31))
                                                   .mainlineSlnid(lineVersion.getSlnid())
                                                   .slnid("ch:1000")
                                                   .build();
    List<SublineVersion> sublineVersions = new ArrayList<>();
    sublineVersions.add(sublineVersion);
    doReturn(lineVersions).when(lineVersionRepository)
                          .findAllBySlnidOrderByValidFrom(lineVersion.getSlnid());
    doReturn(sublineVersions).when(sublineVersionRepository)
                             .getSublineVersionByMainlineSlnid(lineVersion.getSlnid());
    //when
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersion);
    //then
    assertThat(result).isFalse();
  }

  /**
   * 4. Case: Line gap not covers subline
   * Line	    |-----------|    |------------|
   * Subline  |-----------------------------|
   * Result OK
   */
  @Test
  public void shouldReturnFalseWhenLineGapNotCoversSubline() {
    //given
    LineVersion firstLineVersion = LineTestData.lineVersionBuilder()
                                          .validFrom(LocalDate.of(2000, 1, 2))
                                          .validTo(LocalDate.of(2000, 5, 30))
                                          .slnid("ch:1000").build();
    LineVersion secondLineVersion = LineTestData.lineVersionBuilder()
                                          .validFrom(LocalDate.of(2000, 7, 1))
                                          .validTo(LocalDate.of(2000, 12, 31))
                                          .slnid("ch:1000").build();
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(firstLineVersion);
    lineVersions.add(secondLineVersion);
    SublineVersion sublineVersion = SublineTestData.sublineVersionBuilder()
                                                   .validFrom(LocalDate.of(2000, 1, 1))
                                                   .validTo(LocalDate.of(2000, 12, 31))
                                                   .mainlineSlnid(firstLineVersion.getSlnid())
                                                   .slnid("ch:1000")
                                                   .build();
    List<SublineVersion> sublineVersions = new ArrayList<>();
    sublineVersions.add(sublineVersion);
    doReturn(lineVersions).when(lineVersionRepository)
                          .findAllBySlnidOrderByValidFrom(firstLineVersion.getSlnid());
    doReturn(sublineVersions).when(sublineVersionRepository)
                             .getSublineVersionByMainlineSlnid(firstLineVersion.getSlnid());
    //when
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(firstLineVersion);
    //then
    assertThat(result).isFalse();
  }

  /**
   * 5. Case: Subline gap not covers line
   * Line       |-----------------------------|
   * Subline	  |-----------|    |------------|
   * Result OK
   */
  @Test
  public void shouldReturnFalseWhenSubLineGapNotCoversLine() {
    //given
    LineVersion firstLineVersion = LineTestData.lineVersionBuilder()
                                          .validFrom(LocalDate.of(2000, 1, 2))
                                          .validTo(LocalDate.of(2000, 12, 31))
                                          .slnid("ch:1000").build();
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(firstLineVersion);
    SublineVersion firtsSublineVersion = SublineTestData.sublineVersionBuilder()
                                                        .validFrom(LocalDate.of(2000, 1, 2))
                                                        .validTo(LocalDate.of(2000, 5, 30))
                                                   .mainlineSlnid(firstLineVersion.getSlnid())
                                                   .slnid("ch:1000")
                                                   .build();
    SublineVersion secondSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2000, 1, 1))
                                                         .validTo(LocalDate.of(2000, 12, 31))
                                                   .mainlineSlnid(firstLineVersion.getSlnid())
                                                   .slnid("ch:1000")
                                                   .build();
    List<SublineVersion> sublineVersions = new ArrayList<>();
    sublineVersions.add(firtsSublineVersion);
    sublineVersions.add(secondSublineVersion);
    doReturn(lineVersions).when(lineVersionRepository)
                          .findAllBySlnidOrderByValidFrom(firstLineVersion.getSlnid());
    doReturn(sublineVersions).when(sublineVersionRepository)
                             .getSublineVersionByMainlineSlnid(firstLineVersion.getSlnid());
    //when
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(firstLineVersion);
    //then
    assertThat(result).isFalse();
  }

  /**
   * Case: Subline not covers the entire line
   * Line	    |-----------------------------|
   * Subline     |--------------------|
   * Result OK
   */
  @Test
  public void shouldReturnFalseWhenASublineDoesNotCoverTheLineCompletely() {
    //given
    LineVersion lineVersion = LineTestData.lineVersionBuilder()
                                          .validFrom(LocalDate.of(2000, 1, 1))
                                          .validTo(LocalDate.of(2000, 12, 31))
                                          .slnid("ch:1000").build();
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(lineVersion);
    SublineVersion sublineVersion = SublineTestData.sublineVersionBuilder()
                                                   .validFrom(LocalDate.of(2000, 2, 1))
                                                   .validTo(LocalDate.of(2000, 10, 31))
                                                   .mainlineSlnid(lineVersion.getSlnid())
                                                   .slnid("ch:1000")
                                                   .build();
    List<SublineVersion> sublineVersions = new ArrayList<>();
    sublineVersions.add(sublineVersion);
    doReturn(lineVersions).when(lineVersionRepository)
                          .findAllBySlnidOrderByValidFrom(lineVersion.getSlnid());
    doReturn(sublineVersions).when(sublineVersionRepository)
                             .getSublineVersionByMainlineSlnid(lineVersion.getSlnid());
    //when
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersion);
    //then
    assertThat(result).isFalse();
  }

  /**
   * 7. Case: Line and subline have the same gaps, sublines with same Type
   * Line	    |----------------------|       |--------------------------|
   * Subline  |------------------------|   |----------------------|
   * Result OK
   */
  @Test
  public void shouldReturnFalseWhenSublineAndLineDoesNotMatchTheSameGaps() {
    //given
    LineVersion firstLineVersion = LineTestData.lineVersionBuilder()
                                               .validFrom(LocalDate.of(2000, 1, 1))
                                               .validTo(LocalDate.of(2000, 10, 31))
                                               .slnid("ch:1000").build();
    LineVersion secondLineVersion = LineTestData.lineVersionBuilder()
                                                .validFrom(LocalDate.of(2001, 1, 1))
                                                .validTo(LocalDate.of(2001, 12, 31))
                                                .slnid("ch:1000").build();
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(firstLineVersion);
    lineVersions.add(secondLineVersion);
    SublineVersion firstSublineVersion = SublineTestData.sublineVersionBuilder()
                                                        .validFrom(LocalDate.of(2000, 1, 1))
                                                        .validTo(LocalDate.of(2000, 11, 30))
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1000")
                                                        .build();
    SublineVersion secondSublineVersion = SublineTestData.sublineVersionBuilder()
                                                        .validFrom(LocalDate.of(2001, 8, 1))
                                                        .validTo(LocalDate.of(2001, 10, 31))
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1000")
                                                        .build();

    List<SublineVersion> sublineVersions = new ArrayList<>();
    sublineVersions.add(firstSublineVersion);
    sublineVersions.add(secondSublineVersion);
    doReturn(lineVersions).when(lineVersionRepository)
                          .findAllBySlnidOrderByValidFrom(firstLineVersion.getSlnid());
    doReturn(sublineVersions).when(sublineVersionRepository)
                             .getSublineVersionByMainlineSlnid(firstLineVersion.getSlnid());
    //when
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(firstLineVersion);
    //then
    assertThat(result).isFalse();
  }

  /**
   * 8. Case: Line and subline have the same gaps, sublines with same Type
   *	           01.01.2000          31.12.2000		      01.01.2001         31.12.2001
   * Line	      |-----------------------------|        |-----------------------------|
   * 	           01.01.2000          31.12.2000		      01.01.2001         31.12.2001
   * Subline	  |-----------------------------|        |-----------------------------|
   * Result OK
   */
  @Test
  public void shouldReturnTruWhenSublineAndLineAreCompletelyCoveredWithTheSameGaps() {
    //given
    LineVersion firstLineVersion = LineTestData.lineVersionBuilder()
                                               .validFrom(LocalDate.of(2000, 1, 1))
                                               .validTo(LocalDate.of(2000, 12, 31))
                                               .slnid("ch:1000").build();
    LineVersion secondLineVersion = LineTestData.lineVersionBuilder()
                                                .validFrom(LocalDate.of(2001, 1, 1))
                                                .validTo(LocalDate.of(2001, 12, 31))
                                                .slnid("ch:1000").build();
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(firstLineVersion);
    lineVersions.add(secondLineVersion);
    SublineVersion firstSublineVersion = SublineTestData.sublineVersionBuilder()
                                                        .validFrom(LocalDate.of(2000, 1, 1))
                                                        .validTo(LocalDate.of(2000, 12, 31))
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1000")
                                                        .build();
    SublineVersion secondSublineVersion = SublineTestData.sublineVersionBuilder()
                                                        .validFrom(LocalDate.of(2001, 1, 1))
                                                        .validTo(LocalDate.of(2001, 12, 31))
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1000")
                                                        .build();

    List<SublineVersion> sublineVersions = new ArrayList<>();
    sublineVersions.add(firstSublineVersion);
    sublineVersions.add(secondSublineVersion);
    doReturn(lineVersions).when(lineVersionRepository)
                          .findAllBySlnidOrderByValidFrom(firstLineVersion.getSlnid());
    doReturn(sublineVersions).when(sublineVersionRepository)
                             .getSublineVersionByMainlineSlnid(firstLineVersion.getSlnid());
    //when
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(firstLineVersion);
    //then
    assertThat(result).isTrue();
  }

  /**
   * 9. Case: Line fully covered by 2 different sublines with the same Type
   *
   * Line	          |-----------------------------|
   * Subline A1_1   |--------------|                 (Technical)
   * Subline A1_2		               |--------------|  (Technical)
   * Result OK
   */
  @Test
  public void shouldReturnTrueWhenDifferentSubLineWithTheSameTypeCoverALine() {
    //given
    LineVersion firstLineVersion = LineTestData.lineVersionBuilder()
                                               .validFrom(LocalDate.of(2000, 1, 2))
                                               .validTo(LocalDate.of(2000, 12, 31))
                                               .slnid("ch:1000").build();
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(firstLineVersion);
    SublineVersion firtsSublineVersion = SublineTestData.sublineVersionBuilder()
                                                        .validFrom(LocalDate.of(2000, 1, 2))
                                                        .validTo(LocalDate.of(2000, 5, 31))
                                                        .type(SublineType.TECHNICAL)
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1000")
                                                        .build();
    SublineVersion secondSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2000, 6, 1))
                                                         .validTo(LocalDate.of(2000, 12, 31))
                                                         .type(SublineType.TECHNICAL)
                                                         .mainlineSlnid(firstLineVersion.getSlnid())
                                                         .slnid("ch:1001")
                                                         .build();
    List<SublineVersion> sublineVersions = new ArrayList<>();
    sublineVersions.add(firtsSublineVersion);
    sublineVersions.add(secondSublineVersion);
    doReturn(lineVersions).when(lineVersionRepository)
                          .findAllBySlnidOrderByValidFrom(firstLineVersion.getSlnid());
    doReturn(sublineVersions).when(sublineVersionRepository)
                             .getSublineVersionByMainlineSlnid(firstLineVersion.getSlnid());
    //when
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(firstLineVersion);
    //then
    assertThat(result).isTrue();
  }

  /**
   * 10. Case: Line fully covered by 2 different sublines with different Type
   *
   * Line	          |-----------------------------|
   * Subline A1_1   |--------------|                 (Technical)
   * Subline A1_2		               |--------------|  (Compensation)
   * Result OK
   */
  @Test
  public void shouldReturnFalseWhenDifferentSubLineWithDifferentTypesCoverALine() {
    //given
    LineVersion firstLineVersion = LineTestData.lineVersionBuilder()
                                               .validFrom(LocalDate.of(2000, 1, 2))
                                               .validTo(LocalDate.of(2000, 12, 31))
                                               .slnid("ch:1000").build();
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(firstLineVersion);
    SublineVersion firtsSublineVersion = SublineTestData.sublineVersionBuilder()
                                                        .validFrom(LocalDate.of(2000, 1, 2))
                                                        .validTo(LocalDate.of(2000, 5, 30))
                                                        .type(SublineType.TECHNICAL)
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1000")
                                                        .build();
    SublineVersion secondSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2000, 6, 1))
                                                         .validTo(LocalDate.of(2000, 12, 31))
                                                         .type(SublineType.COMPENSATION)
                                                         .mainlineSlnid(firstLineVersion.getSlnid())
                                                         .slnid("ch:1001")
                                                         .build();
    List<SublineVersion> sublineVersions = new ArrayList<>();
    sublineVersions.add(firtsSublineVersion);
    sublineVersions.add(secondSublineVersion);
    doReturn(lineVersions).when(lineVersionRepository)
                          .findAllBySlnidOrderByValidFrom(firstLineVersion.getSlnid());
    doReturn(sublineVersions).when(sublineVersionRepository)
                             .getSublineVersionByMainlineSlnid(firstLineVersion.getSlnid());
    //when
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(firstLineVersion);
    //then
    assertThat(result).isFalse();
  }


}