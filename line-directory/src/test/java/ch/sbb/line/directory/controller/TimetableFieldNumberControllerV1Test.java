package ch.sbb.line.directory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.lidi.TimetableFieldNumberVersionModel;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.service.TimetableFieldNumberService;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TimetableFieldNumberControllerV1Test {

  @Mock
  private TimetableFieldNumberService timetableFieldNumberService;

  @InjectMocks
  private TimetableFieldNumberControllerV1 timetableFieldNumberControllerV1;

  @Captor
  private ArgumentCaptor<TimetableFieldNumberVersion> versionArgumentCaptor;

  private static TimetableFieldNumberVersionModel createModel() {
    return TimetableFieldNumberVersionModel.builder()
        .ttfnid("ch:1:ttfnid:100000")
        .description("FPFN Description")
        .number("BEX")
        .swissTimetableFieldNumber("b0.BEX")
        .validFrom(LocalDate.of(2020, 12, 12))
        .validTo(LocalDate.of(2099, 12, 12))
        .build();
  }

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(timetableFieldNumberService.create(any())).then(
        i -> i.getArgument(0, TimetableFieldNumberVersion.class));
  }

  @Test
  void shouldSaveNewVersion() {
    // given
    TimetableFieldNumberVersionModel timetableFieldNumberVersionModel = createModel();

    // when
    timetableFieldNumberControllerV1.createVersion(timetableFieldNumberVersionModel);

    // then
    verify(timetableFieldNumberService).create(versionArgumentCaptor.capture());
    assertThat(versionArgumentCaptor.getValue()).usingRecursiveComparison()
        .ignoringFields("editor", "creator", "editionDate",
            "creationDate", "lineRelations", "ttfnid",
            "version")
        .isEqualTo(timetableFieldNumberVersionModel);
  }

}
