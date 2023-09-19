package ch.sbb.line.directory.validation;


import static ch.sbb.atlas.api.lidi.enumaration.SublineType.COMPENSATION;
import static ch.sbb.atlas.api.lidi.enumaration.SublineType.TECHNICAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
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

 class CoverageValidationServiceTest {

  @Mock
  private CoverageService coverageService;
  @Mock
  private SublineVersionRepository sublineVersionRepository;
  @Mock
  private LineVersionRepository lineVersionRepository;

  private CoverageValidationService coverageValidationService;

  @BeforeEach()
   void setUp() {
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
   void shouldReturnTrueWhenLineHasNoSublineRelated() {
    //given
    LineVersion lineVersion = LineTestData.lineVersionBuilder().slnid("ch:1000").build();
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(lineVersion);
    //when
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersions, new ArrayList<>());
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
   void shouldReturnTrueWhenALineCoversCompletelyASubline() {
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
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersions,sublineVersions);
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
   void shouldReturnTrueWhenLinesCoverCompletelySublinesWithoutGap() {
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
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersions,sublineVersions);
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
   void shouldReturnFalseWhenSublineRangeIsRightOutsideOfLineRanges() {
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
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersions,sublineVersions);
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
   void shouldReturnFalseWhenSublineRangeIsLeftOutsideOfLineRanges() {
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
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersions,sublineVersions);
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
   void shouldReturnFalseWhenLineGapNotCoversSubline() {
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
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersions,sublineVersions);
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
   void shouldReturnFalseWhenSubLineGapNotCoversLine() {
    //given
    LineVersion lineVersion = LineTestData.lineVersionBuilder()
                                          .validFrom(LocalDate.of(2000, 1, 1))
                                          .validTo(LocalDate.of(2000, 12, 31))
                                          .slnid("ch:1000").build();
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(lineVersion);
    SublineVersion firtsSublineVersion = SublineTestData.sublineVersionBuilder()
                                                        .validFrom(LocalDate.of(2000, 1, 1))
                                                        .validTo(LocalDate.of(2000, 5, 30))
                                                   .mainlineSlnid(lineVersion.getSlnid())
                                                   .slnid("ch:1000")
                                                   .build();
    SublineVersion secondSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2000, 7, 1))
                                                         .validTo(LocalDate.of(2000, 12, 31))
                                                   .mainlineSlnid(lineVersion.getSlnid())
                                                   .slnid("ch:1000")
                                                   .build();
    List<SublineVersion> sublineVersions = new ArrayList<>();
    sublineVersions.add(firtsSublineVersion);
    sublineVersions.add(secondSublineVersion);
    doReturn(lineVersions).when(lineVersionRepository)
                          .findAllBySlnidOrderByValidFrom(lineVersion.getSlnid());
    doReturn(sublineVersions).when(sublineVersionRepository)
                             .getSublineVersionByMainlineSlnid(lineVersion.getSlnid());
    //when
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersions,sublineVersions);
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
   void shouldReturnFalseWhenASublineDoesNotCoverTheLineCompletely() {
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
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersions,sublineVersions);
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
   void shouldReturnFalseWhenSublineAndLineDoesNotMatchTheSameGaps() {
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
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersions,sublineVersions);
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
   void shouldReturnTruWhenSublineAndLineAreCompletelyCoveredWithTheSameGaps() {
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
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersions,sublineVersions);
    //then
    assertThat(result).isTrue();
  }
  /**
   * 8. Case: Line and subline have the same gaps, sublines with same Type
   *	           01.01.1900          01.01.1900		      31.12.2000         31.12.2000     31.12.2000         31.12.2000
   * Line	      |-----------------------------|        |-----------------------------|    |-----------------------------|
   * 	           01.01.1900          01.01.1900		      30.12.2000         31.12.2000     30.12.2000         31.12.2000
   * Subline	  |-----------------------------|       |------------------------------|   |------------------------------|
   * Result NOK
   */
  @Test
   void shouldReturnFalseWhenSublineAndLineAreCompletelyCoveredWithTheSameGaps() {
    //given
    LineVersion firstLineVersion = LineTestData.lineVersionBuilder()
                                               .validFrom(LocalDate.of(1900, 1, 1))
                                               .validTo(LocalDate.of(1900, 1, 1))
                                               .slnid("ch:1000").build();
    LineVersion secondLineVersion = LineTestData.lineVersionBuilder()
                                                .validFrom(LocalDate.of(2000, 12, 31))
                                                .validTo(LocalDate.of(2000, 12, 31))
                                                .slnid("ch:1000").build();
    LineVersion thirdLineVersion = LineTestData.lineVersionBuilder()
                                                .validFrom(LocalDate.of(2099, 12, 31))
                                                .validTo(LocalDate.of(2099, 12, 31))
                                                .slnid("ch:1000").build();
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(firstLineVersion);
    lineVersions.add(secondLineVersion);
    lineVersions.add(thirdLineVersion);
    SublineVersion firstSublineVersion = SublineTestData.sublineVersionBuilder()
                                                        .validFrom(LocalDate.of(1900, 1, 1))
                                                        .validTo(LocalDate.of(1900, 1, 1))
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1000")
                                                        .build();
    SublineVersion secondSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2000, 12, 30))
                                                         .validTo(LocalDate.of(2000,12, 31))
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1000")
                                                        .build();
    SublineVersion thirdSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2099, 12, 30))
                                                         .validTo(LocalDate.of(2099, 12, 31))
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
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersions,sublineVersions);
    //then
    assertThat(result).isFalse();
  }

  /**
   * 8. Case: Line and subline have the same gaps, sublines with same Type
   *	           01.01.1900          01.01.1900		      30.12.2000         31.12.2000
   * Line	      |-----------------------------|        |-----------------------------|
   * 	           01.01.1900          01.01.1900		      30.12.2000         30.12.2000 31.12.2000         31.12.2000
   * Subline	  |-----------------------------|       |------------------------------|------------------------------|
   * Result NOK
   */
  @Test
   void shouldReturnTrueWhenSublineAndLineAreCompletelyCoveredWithTheSameGap() {
    //given
    LineVersion firstLineVersion = LineTestData.lineVersionBuilder()
                                               .validFrom(LocalDate.of(1900, 1, 1))
                                               .validTo(LocalDate.of(1900, 1, 1))
                                               .slnid("ch:1000").build();
    LineVersion secondLineVersion = LineTestData.lineVersionBuilder()
                                                .validFrom(LocalDate.of(2000, 12, 30))
                                                .validTo(LocalDate.of(2000, 12, 31))
                                                .slnid("ch:1000").build();
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(firstLineVersion);
    lineVersions.add(secondLineVersion);
    SublineVersion firstSublineVersion = SublineTestData.sublineVersionBuilder()
                                                        .validFrom(LocalDate.of(1900, 1, 1))
                                                        .validTo(LocalDate.of(1900, 1, 1))
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1000")
                                                        .build();
    SublineVersion secondSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2000, 12, 30))
                                                         .validTo(LocalDate.of(2000,12, 30))
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1000")
                                                        .build();
    SublineVersion thirdSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2000, 12, 31))
                                                         .validTo(LocalDate.of(2000, 12, 31))
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
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersions,sublineVersions);
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
   void shouldReturnTrueWhenDifferentSubLineWithTheSameTypeCoverALine() {
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
                                                        .sublineType(TECHNICAL)
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1000")
                                                        .build();
    SublineVersion secondSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2000, 6, 1))
                                                         .validTo(LocalDate.of(2000, 12, 31))
                                                         .sublineType(TECHNICAL)
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
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersions,sublineVersions);
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
   void shouldReturnFalseWhenDifferentSubLineWithDifferentTypesCoverALine() {
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
                                                        .sublineType(TECHNICAL)
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1000")
                                                        .build();
    SublineVersion secondSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2000, 6, 1))
                                                         .validTo(LocalDate.of(2000, 12, 31))
                                                         .sublineType(COMPENSATION)
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
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersions,sublineVersions);
    //then
    assertThat(result).isFalse();
  }

  /**
   * 11. Case: Line fully covered by 3 different sublines with the same Type
   *
   * Line	          |-----------------------------|
   * Subline A1_1   |--------------|                 (Technical)
   * Subline A1_2		               |--------------|  (Technical)
   * Subline A1_2		               |--------------|  (Technical)
   * Result OK
   */
  @Test
   void shouldReturnTrueWhenDifferentSubLineWithTheSameTypesCoverALine() {
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
                                                        .sublineType(TECHNICAL)
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1000")
                                                        .build();
    SublineVersion secondSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2000, 6, 1))
                                                         .validTo(LocalDate.of(2000, 12, 31))
                                                         .sublineType(TECHNICAL)
                                                         .mainlineSlnid(firstLineVersion.getSlnid())
                                                         .slnid("ch:1001")
                                                         .build();
    SublineVersion thirdSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2000, 6, 1))
                                                         .validTo(LocalDate.of(2000, 12, 31))
                                                         .sublineType(TECHNICAL)
                                                         .mainlineSlnid(firstLineVersion.getSlnid())
                                                         .slnid("ch:1002")
                                                         .build();
    List<SublineVersion> sublineVersions = new ArrayList<>();
    sublineVersions.add(firtsSublineVersion);
    sublineVersions.add(secondSublineVersion);
    sublineVersions.add(thirdSublineVersion);
    doReturn(lineVersions).when(lineVersionRepository)
                          .findAllBySlnidOrderByValidFrom(firstLineVersion.getSlnid());
    doReturn(sublineVersions).when(sublineVersionRepository)
                             .getSublineVersionByMainlineSlnid(firstLineVersion.getSlnid());
    //when
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersions,sublineVersions);
    //then
    assertThat(result).isTrue();
  }

  /**
   * 11-a. Case: Line fully covered by 3 different sublines with the same Type
   *
   * Line	          |-----------------------------|
   * Subline A1_1   |--------------|                 (Technical)
   * Subline A1_2		    |-------|                    (Technical)
   * Subline A1_2		               |--------------|  (Technical)
   * Result OK
   */
  @Test
   void shouldReturnTrueWhenDifferentSubLineWithTheSameTypesCoverALineDifferent() {
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
                                                        .sublineType(TECHNICAL)
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1000")
                                                        .build();
    SublineVersion secondSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2000, 3, 1))
                                                         .validTo(LocalDate.of(2000, 4, 30))
                                                         .sublineType(TECHNICAL)
                                                         .mainlineSlnid(firstLineVersion.getSlnid())
                                                         .slnid("ch:1001")
                                                         .build();
    SublineVersion thirdSublineVersion = SublineTestData.sublineVersionBuilder()
                                                        .validFrom(LocalDate.of(2000, 6, 1))
                                                        .validTo(LocalDate.of(2000, 12, 31))
                                                        .sublineType(TECHNICAL)
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1002")
                                                        .build();
    List<SublineVersion> sublineVersions = new ArrayList<>();
    sublineVersions.add(firtsSublineVersion);
    sublineVersions.add(secondSublineVersion);
    sublineVersions.add(thirdSublineVersion);
    doReturn(lineVersions).when(lineVersionRepository)
                          .findAllBySlnidOrderByValidFrom(firstLineVersion.getSlnid());
    doReturn(sublineVersions).when(sublineVersionRepository)
                             .getSublineVersionByMainlineSlnid(firstLineVersion.getSlnid());
    //when
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersions,sublineVersions);
    //then
    assertThat(result).isTrue();
  }

  /**
   * 11-b. Case: Line fully covered by 5 different overlapped sublines with the same Type
   *
   * Line	          |-----------------------------|
   * Subline A1_1   |--------------|                 (Technical)
   * Subline A1_2		  |----|                         (Technical)
   * Subline A1_3		    |-------|                    (Technical)
   * Subline A1_4		               |--------------|  (Technical)
   * Subline A1_5		                 |----------|    (Technical)
   * Result OK
   */
  @Test
   void shouldReturnTrueWhenDifferentSubLineWithTheSameTypesCoverAndMultipleOverlappingALineDifferent() {
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
                                                        .sublineType(TECHNICAL)
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1000")
                                                        .build();
    SublineVersion fourthSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2000, 2, 1))
                                                         .validTo(LocalDate.of(2000, 2, 28))
                                                         .sublineType(TECHNICAL)
                                                         .mainlineSlnid(firstLineVersion.getSlnid())
                                                         .slnid("ch:1001")
                                                         .build();
    SublineVersion secondSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2000, 3, 1))
                                                         .validTo(LocalDate.of(2000, 4, 30))
                                                         .sublineType(TECHNICAL)
                                                         .mainlineSlnid(firstLineVersion.getSlnid())
                                                         .slnid("ch:1001")
                                                         .build();
    SublineVersion thirdSublineVersion = SublineTestData.sublineVersionBuilder()
                                                        .validFrom(LocalDate.of(2000, 6, 1))
                                                        .validTo(LocalDate.of(2000, 12, 31))
                                                        .sublineType(TECHNICAL)
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1002")
                                                        .build();
    SublineVersion fifthSublineVersion = SublineTestData.sublineVersionBuilder()
                                                        .validFrom(LocalDate.of(2000, 7, 1))
                                                        .validTo(LocalDate.of(2000, 11, 30))
                                                        .sublineType(TECHNICAL)
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1002")
                                                        .build();
    List<SublineVersion> sublineVersions = new ArrayList<>();
    sublineVersions.add(firtsSublineVersion);
    sublineVersions.add(secondSublineVersion);
    sublineVersions.add(thirdSublineVersion);
    sublineVersions.add(fourthSublineVersion);
    sublineVersions.add(fifthSublineVersion);
    doReturn(lineVersions).when(lineVersionRepository)
                          .findAllBySlnidOrderByValidFrom(firstLineVersion.getSlnid());
    doReturn(sublineVersions).when(sublineVersionRepository)
                             .getSublineVersionByMainlineSlnid(firstLineVersion.getSlnid());
    //when
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersions,sublineVersions);
    //then
    assertThat(result).isTrue();
  }

  /**
   * 11-b. Case: Line fully covered by 5 different overlapped sublines with the same Type
   *
   * Line	          |-----------------------------|
   * Subline A1_1   |----|                           (Technical)
   * Subline A1_2		  |------|                       (Technical)
   * Subline A1_3		    |-------|                    (Technical)
   * Subline A1_4		            |--------------|     (Technical)
   * Subline A1_5		                   |----------|  (Technical)
   * Result OK
   */
  @Test
   void shouldReturnTrueWhenDifferentSubLineCoverAMultipleOverlappedALines() {
    //given
    LineVersion firstLineVersion = LineTestData.lineVersionBuilder()
                                               .validFrom(LocalDate.of(2000, 1, 1))
                                               .validTo(LocalDate.of(2000, 12, 31))
                                               .slnid("ch:1000").build();
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(firstLineVersion);
    SublineVersion firtsSublineVersion = SublineTestData.sublineVersionBuilder()
                                                        .validFrom(LocalDate.of(2000, 1, 1))
                                                        .validTo(LocalDate.of(2000, 2, 27))
                                                        .sublineType(TECHNICAL)
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1000")
                                                        .build();
    SublineVersion fourthSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2000, 2, 15))
                                                         .validTo(LocalDate.of(2000, 3, 31))
                                                         .sublineType(TECHNICAL)
                                                         .mainlineSlnid(firstLineVersion.getSlnid())
                                                         .slnid("ch:1001")
                                                         .build();
    SublineVersion secondSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2000, 3, 15))
                                                         .validTo(LocalDate.of(2000, 4, 30))
                                                         .sublineType(TECHNICAL)
                                                         .mainlineSlnid(firstLineVersion.getSlnid())
                                                         .slnid("ch:1001")
                                                         .build();
    SublineVersion thirdSublineVersion = SublineTestData.sublineVersionBuilder()
                                                        .validFrom(LocalDate.of(2000, 4, 30))
                                                        .validTo(LocalDate.of(2000, 10, 31))
                                                        .sublineType(TECHNICAL)
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1002")
                                                        .build();
    SublineVersion fifthSublineVersion = SublineTestData.sublineVersionBuilder()
                                                        .validFrom(LocalDate.of(2000, 10, 15))
                                                        .validTo(LocalDate.of(2000, 12, 31))
                                                        .sublineType(TECHNICAL)
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1002")
                                                        .build();
    List<SublineVersion> sublineVersions = new ArrayList<>();
    sublineVersions.add(firtsSublineVersion);
    sublineVersions.add(secondSublineVersion);
    sublineVersions.add(thirdSublineVersion);
    sublineVersions.add(fourthSublineVersion);
    sublineVersions.add(fifthSublineVersion);
    doReturn(lineVersions).when(lineVersionRepository)
                          .findAllBySlnidOrderByValidFrom(firstLineVersion.getSlnid());
    doReturn(sublineVersions).when(sublineVersionRepository)
                             .getSublineVersionByMainlineSlnid(firstLineVersion.getSlnid());
    //when
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersions,sublineVersions);
    //then
    assertThat(result).isTrue();
  }

  /**
   * 11-b. Case: Line fully covered by different sublines
   *
   * Line	          |-----------------------------|
   * Subline A1_1   |----|                           (Technical)
   * Subline A1_2		  |------|                       (Technical)
   * Subline A1_3		    |-------|                    (Technical)
   * Subline A1_4		            |--------------|     (Compensation)
   * Subline A1_5		                   |----------|  (Technical)
   * Result NOK
   */
  @Test
   void shouldReturnFalseWhenDifferentSubLineCoverAMultipleOverlappedALines() {
    //given
    LineVersion firstLineVersion = LineTestData.lineVersionBuilder()
                                               .validFrom(LocalDate.of(2000, 1, 1))
                                               .validTo(LocalDate.of(2000, 12, 31))
                                               .slnid("ch:1000").build();
    List<LineVersion> lineVersions = new ArrayList<>();
    lineVersions.add(firstLineVersion);
    SublineVersion firtsSublineVersion = SublineTestData.sublineVersionBuilder()
                                                        .validFrom(LocalDate.of(2000, 1, 1))
                                                        .validTo(LocalDate.of(2000, 2, 27))
                                                        .sublineType(TECHNICAL)
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1000")
                                                        .build();
    SublineVersion fourthSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2000, 2, 15))
                                                         .validTo(LocalDate.of(2000, 3, 31))
                                                         .sublineType(TECHNICAL)
                                                         .mainlineSlnid(firstLineVersion.getSlnid())
                                                         .slnid("ch:1001")
                                                         .build();
    SublineVersion secondSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2000, 3, 15))
                                                         .validTo(LocalDate.of(2000, 4, 30))
                                                         .sublineType(TECHNICAL)
                                                         .mainlineSlnid(firstLineVersion.getSlnid())
                                                         .slnid("ch:1001")
                                                         .build();
    SublineVersion thirdSublineVersion = SublineTestData.sublineVersionBuilder()
                                                        .validFrom(LocalDate.of(2000, 4, 30))
                                                        .validTo(LocalDate.of(2000, 10, 31))
                                                        .sublineType(COMPENSATION)
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1002")
                                                        .build();
    SublineVersion fifthSublineVersion = SublineTestData.sublineVersionBuilder()
                                                        .validFrom(LocalDate.of(2000, 10, 15))
                                                        .validTo(LocalDate.of(2000, 12, 31))
                                                        .sublineType(TECHNICAL)
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1002")
                                                        .build();
    List<SublineVersion> sublineVersions = new ArrayList<>();
    sublineVersions.add(firtsSublineVersion);
    sublineVersions.add(secondSublineVersion);
    sublineVersions.add(thirdSublineVersion);
    sublineVersions.add(fourthSublineVersion);
    sublineVersions.add(fifthSublineVersion);
    doReturn(lineVersions).when(lineVersionRepository)
                          .findAllBySlnidOrderByValidFrom(firstLineVersion.getSlnid());
    doReturn(sublineVersions).when(sublineVersionRepository)
                             .getSublineVersionByMainlineSlnid(firstLineVersion.getSlnid());
    //when
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersions,sublineVersions);
    //then
    assertThat(result).isFalse();
  }

  /**
   * 11-c. Case: Line fully covered by 3 different sublines with different Type
   *
   * Line	          |-----------------------------|
   * Subline A1_1   |--------------|                 (Technical)
   * Subline A1_2		               |--------------|  (Technical)
   * Subline A1_2		               |--------------|  (Compensation)
   * Result OK
   */
  @Test
   void shouldReturnFalseWhenDifferentSubLineWithDifferentTypesCoverLine() {
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
                                                        .sublineType(TECHNICAL)
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1000")
                                                        .build();
    SublineVersion secondSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2000, 6, 1))
                                                         .validTo(LocalDate.of(2000, 12, 31))
                                                         .sublineType(TECHNICAL)
                                                         .mainlineSlnid(firstLineVersion.getSlnid())
                                                         .slnid("ch:1001")
                                                         .build();
    SublineVersion thirdSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2000, 6, 1))
                                                         .validTo(LocalDate.of(2000, 12, 31))
                                                         .sublineType(COMPENSATION)
                                                         .mainlineSlnid(firstLineVersion.getSlnid())
                                                         .slnid("ch:1002")
                                                         .build();
    List<SublineVersion> sublineVersions = new ArrayList<>();
    sublineVersions.add(firtsSublineVersion);
    sublineVersions.add(secondSublineVersion);
    sublineVersions.add(thirdSublineVersion);
    doReturn(lineVersions).when(lineVersionRepository)
                          .findAllBySlnidOrderByValidFrom(firstLineVersion.getSlnid());
    doReturn(sublineVersions).when(sublineVersionRepository)
                             .getSublineVersionByMainlineSlnid(firstLineVersion.getSlnid());
    //when
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersions,sublineVersions);
    //then
    assertThat(result).isFalse();
  }

  /**
   * 11-d. Case: Line fully covered by 3 different sublines with different Type
   *
   * Line	          |-----------------------------|
   * Subline A1_1   |--------------|                 (Technical)
   * Subline A2_1		               |--------------|  (Technical)
   * Subline A3_1		|--------------|                 (Compensation)
   * Subline A4_1		               |--------------|  (Compensation)
   * Result OK
   */
  @Test
   void shouldReturnFalseWhenDifferentSubLineWithDifferentTypesCoverLine11d() {
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
                                                        .sublineType(TECHNICAL)
                                                        .mainlineSlnid(firstLineVersion.getSlnid())
                                                        .slnid("ch:1000")
                                                        .build();
    SublineVersion secondSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2000, 6, 1))
                                                         .validTo(LocalDate.of(2000, 12, 31))
                                                         .sublineType(TECHNICAL)
                                                         .mainlineSlnid(firstLineVersion.getSlnid())
                                                         .slnid("ch:1001")
                                                         .build();
    SublineVersion thirdSublineVersion = SublineTestData.sublineVersionBuilder()
                                                        .validFrom(LocalDate.of(2000, 1, 2))
                                                        .validTo(LocalDate.of(2000, 5, 31))
                                                         .sublineType(COMPENSATION)
                                                         .mainlineSlnid(firstLineVersion.getSlnid())
                                                         .slnid("ch:1002")
                                                         .build();
    SublineVersion fourthSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .validFrom(LocalDate.of(2000, 6, 1))
                                                         .validTo(LocalDate.of(2000, 12, 31))
                                                         .sublineType(COMPENSATION)
                                                         .mainlineSlnid(firstLineVersion.getSlnid())
                                                         .slnid("ch:1002")
                                                         .build();
    List<SublineVersion> sublineVersions = new ArrayList<>();
    sublineVersions.add(firtsSublineVersion);
    sublineVersions.add(secondSublineVersion);
    sublineVersions.add(thirdSublineVersion);
    sublineVersions.add(fourthSublineVersion);
    doReturn(lineVersions).when(lineVersionRepository)
                          .findAllBySlnidOrderByValidFrom(firstLineVersion.getSlnid());
    doReturn(sublineVersions).when(sublineVersionRepository)
                             .getSublineVersionByMainlineSlnid(firstLineVersion.getSlnid());
    //when
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersions,sublineVersions);
    //then
    assertThat(result).isTrue();
  }

  /**
   * 12. Case: Lines with gap without sublines
   *	           01.01.2000          31.12.2000		      01.01.2001         31.12.2001
   * Line	      |-----------------------------|        |-----------------------------|
   * Result OK
   */
  @Test
   void shouldReturnTruWhenLineAreWithGapWithoutSublines() {
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


    List<SublineVersion> sublineVersions = new ArrayList<>();
    doReturn(lineVersions).when(lineVersionRepository)
                          .findAllBySlnidOrderByValidFrom(firstLineVersion.getSlnid());
    doReturn(sublineVersions).when(sublineVersionRepository)
                             .getSublineVersionByMainlineSlnid(firstLineVersion.getSlnid());
    //when
    boolean result = coverageValidationService.areLinesAndSublinesCompletelyCovered(lineVersions,sublineVersions);
    //then
    assertThat(result).isTrue();
  }

}