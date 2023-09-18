package ch.sbb.line.directory.service;


import static ch.sbb.atlas.api.lidi.enumaration.CoverageType.COMPLETE;
import static ch.sbb.atlas.api.lidi.enumaration.ModelType.LINE;
import static ch.sbb.atlas.api.lidi.enumaration.ModelType.SUBLINE;
import static ch.sbb.atlas.api.lidi.enumaration.ValidationErrorType.LINE_RANGE_SMALLER_THEN_SUBLINE_RANGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.entity.Coverage;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.repository.CoverageRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

 class CoverageServiceTest {

  @Mock
  private CoverageRepository coverageRepository;

  private CoverageService coverageService;

  @BeforeEach
   void setUp() {
    MockitoAnnotations.openMocks(this);
    coverageService = new CoverageService(coverageRepository);
  }

  @Test
   void shouldCoverageComplete() {
    //given
    LineVersion lineVersion = LineTestData.lineVersionBuilder().slnid("ch:1000").build();
    SublineVersion firstSublineVersion = SublineTestData.sublineVersionBuilder()
                                                        .slnid("ch:1001")
                                                        .mainlineSlnid(lineVersion.getSlnid())
                                                        .build();
    SublineVersion secondSublineVersion = SublineTestData.sublineVersionBuilder()
                                                         .slnid("ch:1001")
                                                         .description("Ciao")
                                                         .mainlineSlnid(lineVersion.getSlnid())
                                                         .build();
    List<SublineVersion> sublineVersions = new ArrayList<>();
    sublineVersions.add(firstSublineVersion);
    sublineVersions.add(secondSublineVersion);

    //when
    coverageService.updateLineSublineCoverage(lineVersion, sublineVersions, true);

    //then
    verify(coverageRepository, times(1)).findSublineCoverageBySlnidAndModelType("ch:1000", LINE);
    verify(coverageRepository, times(2)).findSublineCoverageBySlnidAndModelType("ch:1001", SUBLINE);
    verify(coverageRepository, times(3)).save(any(Coverage.class));
  }

  @Test
   void shoouldGetSublineCoverageBySlnidAndLineModelType() {
    //given
    String slnid = "ch:1000";
    Coverage coverage = Coverage.builder()
                                .coverageType(COMPLETE)
                                .modelType(LINE)
                                .validationErrorType(LINE_RANGE_SMALLER_THEN_SUBLINE_RANGE)
                                .slnid(slnid)
                                .build();
    doReturn(coverage).when(coverageRepository).findSublineCoverageBySlnidAndModelType(slnid, LINE);
    //when
    Coverage result = coverageService.getSublineCoverageBySlnidAndLineModelType(slnid);

    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(coverage);
  }

  @Test
   void shouldGetSublineCoverageBySlnidAndSublineModelType() {
    //given
    String slnid = "ch:1000";
    Coverage coverage = Coverage.builder()
                                .coverageType(COMPLETE)
                                .modelType(SUBLINE)
                                .validationErrorType(LINE_RANGE_SMALLER_THEN_SUBLINE_RANGE)
                                .slnid(slnid)
                                .build();
    doReturn(coverage).when(coverageRepository)
                      .findSublineCoverageBySlnidAndModelType(slnid, SUBLINE);
    //when
    Coverage result = coverageService.getSublineCoverageBySlnidAndSublineModelType(slnid);

    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(coverage);
  }

  @Test
   void getThrowSlnidNotFoundExceptionWhenLineCoverageDoesNotExists() {
    //given
    String slnid = "ch:1000";
    doReturn(null).when(coverageRepository).findSublineCoverageBySlnidAndModelType(slnid, LINE);
    //when
    assertThatExceptionOfType(SlnidNotFoundException.class).isThrownBy(
        () -> coverageService.getSublineCoverageBySlnidAndLineModelType(slnid));

  }

  @Test
   void getThrowSlnidNotFoundExceptionWhenSublineCoverageDoesNotExists() {
    //given
    String slnid = "ch:1000";
    doReturn(null).when(coverageRepository).findSublineCoverageBySlnidAndModelType(slnid, SUBLINE);
    //when
    assertThatExceptionOfType(SlnidNotFoundException.class).isThrownBy(
        () -> coverageService.getSublineCoverageBySlnidAndLineModelType(slnid));

  }

}