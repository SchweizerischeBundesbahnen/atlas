package ch.sbb.line.directory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.lidi.SublineVersionModel;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.service.SublineService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SublineControllerV1Test {

  private static final String[] RECURSIVE_COMPARISION_IGNORE_FIELDS = {"editor", "creator",
      "editionDate", "creationDate", "version", "etagVersion"};

  @Mock
  private SublineService sublineService;

  private SublineControllerV1 sublineControllerV1;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    sublineControllerV1 = new SublineControllerV1(sublineService);
    when(sublineService.create(any())).then(i -> i.getArgument(0, SublineVersion.class));
  }

  @Test
  void shouldGetSubline() {
    // Given
    SublineVersion sublineVersion = SublineTestData.sublineVersion();
    when(sublineService.findSubline(any())).thenReturn(Collections.singletonList(sublineVersion));

    // When
    List<SublineVersionModel> subline = sublineControllerV1.getSublineVersion("slnid");

    // Then
    assertThat(subline).isNotNull();
    assertThat(subline).hasSize(1)
        .first()
        .usingRecursiveComparison()
        .ignoringFields(RECURSIVE_COMPARISION_IGNORE_FIELDS)
        .isEqualTo(sublineVersion);
  }

}
