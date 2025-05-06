package ch.sbb.line.directory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.lidi.LineModel;
import ch.sbb.atlas.api.lidi.LineRequestParams;
import ch.sbb.atlas.api.lidi.LineVersionModel;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.service.LineService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

class LineControllerV1Test {

  private static final String[] RECURSIVE_COMPARISION_IGNORE_FIELDS = {"editor", "creator",
      "editionDate", "creationDate", "version"};

  @Mock
  private LineService lineService;

  private LineControllerV1 lineControllerV1;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    lineControllerV1 = new LineControllerV1(lineService);
    when(lineService.create(any())).then(i -> i.getArgument(0, LineVersion.class));
  }

  @Test
  void shouldGetLines() {
    // Given
    Line line = LineTestData.line();
    when(lineService.findAll(any())).thenReturn(new PageImpl<>(Collections.singletonList(line)));

    // When
    Container<LineModel> lineContainer = lineControllerV1.getLines(
        Pageable.unpaged(), LineRequestParams.builder().build());

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
    LineModel result = lineControllerV1.getLine("slnid");

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
    when(lineService.findLineVersionsForV1(any())).thenReturn(Collections.singletonList(lineVersion));

    // When
    List<LineVersionModel> line = lineControllerV1.getLineVersions("slnid");

    // Then
    assertThat(line).isNotNull();
    assertThat(line).hasSize(1)
        .first()
        .usingRecursiveComparison()
        .ignoringFields(RECURSIVE_COMPARISION_IGNORE_FIELDS)
        .ignoringFieldsMatchingRegexes("color.*", "etagVersion")
        .isEqualTo(lineVersion);
  }

}
