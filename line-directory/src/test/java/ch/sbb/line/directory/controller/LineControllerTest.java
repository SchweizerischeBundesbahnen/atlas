package ch.sbb.line.directory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.amazon.controller.AmazonController;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.api.Container;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.api.LineModel;
import ch.sbb.line.directory.api.LineVersionModel;
import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.model.CmykColor;
import ch.sbb.line.directory.model.RgbColor;
import ch.sbb.line.directory.service.CoverageService;
import ch.sbb.line.directory.service.LineService;
import ch.sbb.line.directory.service.export.ExportService;
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

public class LineControllerTest {

  private static final RgbColor RGB_COLOR = new RgbColor(0, 0, 0);
  private static final CmykColor CYMK_COLOR = new CmykColor(0, 0, 0, 0);

  private static final String[] RECURSIVE_COMPARISION_IGNORE_FIELDS = {"editor", "creator",
      "editionDate", "creationDate", "version"};

  @Mock
  private LineService lineService;
  @Mock
  private CoverageService coverageService;
  @Mock
  private AmazonController amazonController;
  @Mock
  private ExportService exportService;
  private LineController lineController;

  @Captor
  private ArgumentCaptor<LineVersion> versionArgumentCaptor;

  private static LineVersionModel createModel() {
    return LineTestData.lineVersionModelBuilder()
                       .status(Status.ACTIVE)
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
    lineController = new LineController(lineService, coverageService, amazonController,
        exportService);
    when(lineService.save(any())).then(i -> i.getArgument(0, LineVersion.class));
  }

  @Test
  public void shouldSaveNewVersion() {
    // Given
    LineVersionModel lineVersionModel = createModel();

    // When
    lineController.createLineVersion(lineVersionModel);

    // Then
    verify(lineService).save(versionArgumentCaptor.capture());
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
        Collections.emptyList(), Optional.empty());

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
    verify(lineService).updateVersion(any(), any());
  }
}
