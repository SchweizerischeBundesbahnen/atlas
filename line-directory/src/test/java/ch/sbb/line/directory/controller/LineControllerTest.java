package ch.sbb.line.directory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.lidi.LineModel;
import ch.sbb.atlas.api.lidi.LineVersionModel;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.PaymentType;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.service.CoverageService;
import ch.sbb.line.directory.service.LineService;
import ch.sbb.line.directory.service.LineVersionSnapshotService;
import ch.sbb.line.directory.service.export.LineVersionExportService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

 class LineControllerTest {

  private static final String[] RECURSIVE_COMPARISION_IGNORE_FIELDS = {"editor", "creator",
      "editionDate", "creationDate", "version"};

  @Mock
  private LineService lineService;
  @Mock
  private CoverageService coverageService;

  @Mock
  private LineVersionExportService lineVersionExportService;
  @Mock
  private LineVersionSnapshotService lineVersionSnapshotService;
  private LineController lineController;

  @Captor
  private ArgumentCaptor<LineVersion> versionArgumentCaptor;

  private static LineVersionModel createModel() {
    return LineTestData.lineVersionModelBuilder()
        .status(Status.VALIDATED)
        .lineType(LineType.ORDERLY)
        .slnid("slnid")
        .paymentType(PaymentType.INTERNATIONAL)
        .number("number")
        .alternativeName("alternativeName")
        .combinationName("combinationName")
        .longName("longName")
        .colorFontRgb("#FFFFFF")
        .colorBackRgb("#FFFFFF")
        .colorFontCmyk("10,0,100,7")
        .colorBackCmyk("10,0,100,7")
        .description("description")
        .validFrom(LocalDate.of(2020, 12, 12))
        .validTo(LocalDate.of(2099, 12, 12))
        .businessOrganisation("businessOrganisation")
        .comment("comment")
        .swissLineNumber("swissLineNumber")
        .build();
  }

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    lineController = new LineController(lineService, coverageService, lineVersionExportService, lineVersionSnapshotService);
    when(lineService.create(any())).then(i -> i.getArgument(0, LineVersion.class));
  }

  @Test
   void shouldSaveNewVersion() {
    // Given
    LineVersionModel lineVersionModel = createModel();

    // When
    lineController.createLineVersion(lineVersionModel);

    // Then
    verify(lineService).create(versionArgumentCaptor.capture());
    assertThat(versionArgumentCaptor.getValue()).usingRecursiveComparison()
        .ignoringFields(RECURSIVE_COMPARISION_IGNORE_FIELDS)
        .ignoringFieldsMatchingRegexes("color.*")
        .isEqualTo(lineVersionModel);
  }

  @Test
  void shouldGetLines() {
    // Given
    Line line = LineTestData.line();
    when(lineService.findAll(any())).thenReturn(new PageImpl<>(Collections.singletonList(line)));

    // When
    Container<LineModel> lineContainer = lineController.getLines(
        Pageable.unpaged(), Optional.empty(), Collections.emptyList(), Collections.emptyList(),
        Collections.emptyList(), Optional.empty(), Optional.empty());

    // Then
    assertThat(lineContainer).isNotNull();
    assertThat(lineContainer.getObjects()).hasSize(1)
        .first()
        .usingRecursiveComparison()
        .ignoringFields(RECURSIVE_COMPARISION_IGNORE_FIELDS)
        .isEqualTo(line);
    assertThat(lineContainer.getTotalCount()).isEqualTo(1);
  }

  @Test
  void shouldGetLine() {
    // Given
    Line line = LineTestData.line();
    when(lineService.findLine(any())).thenReturn(Optional.of(line));

    // When
    LineModel result = lineController.getLine("slnid");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).usingRecursiveComparison()
        .ignoringFields(RECURSIVE_COMPARISION_IGNORE_FIELDS)
        .ignoringFieldsMatchingRegexes("color.*")
        .isEqualTo(line);
  }

  @Test
  void shouldGetLineVersions() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersion();
    when(lineService.findLineVersions(any())).thenReturn(Collections.singletonList(lineVersion));

    // When
    List<LineVersionModel> line = lineController.getLineVersions("slnid");

    // Then
    assertThat(line).isNotNull();
    assertThat(line).hasSize(1)
        .first()
        .usingRecursiveComparison()
        .ignoringFields(RECURSIVE_COMPARISION_IGNORE_FIELDS)
        .ignoringFieldsMatchingRegexes("color.*", "etagVersion")
        .isEqualTo(lineVersion);
  }

  @Test
  void shouldDeleteVersion() {
    // Given
    String slnid = "ch:1:slnid:10000";
    // When
    lineController.deleteLines(slnid);

    // Then
    verify(lineService).deleteAll(slnid);
  }

  @Test
  void shouldUpdateVersionWithVersioning() {
    // Given
    LineVersion lineVersion = LineTestData.lineVersion();
    LineVersionModel lineVersionModel = createModel();
    lineVersionModel.setNumber("New name");

    when(lineService.findById(anyLong())).thenReturn(Optional.of(lineVersion));

    // When
    lineController.updateLineVersion(1L, lineVersionModel);

    // Then
    verify(lineService).update(any(), any(), any());
  }

}
