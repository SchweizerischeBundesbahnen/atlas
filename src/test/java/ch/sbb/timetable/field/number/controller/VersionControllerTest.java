package ch.sbb.timetable.field.number.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.timetable.field.number.api.VersionModel;
import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.repository.VersionRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class VersionControllerTest {

  @Mock
  private VersionRepository versionRepository;

  private VersionController versionController;

  @Captor
  private ArgumentCaptor<Version> versionArgumentCaptor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    versionController = new VersionController(versionRepository);
    when(versionRepository.save(any())).then(i -> i.getArgument(0, Version.class));
  }

  @Test
  void shouldSaveNewVersion() {
    // Given
    VersionModel versionModel = createModel();

    // When
    versionController.createVersion(versionModel);

    // Then
    verify(versionRepository).save(versionArgumentCaptor.capture());
    assertThat(versionArgumentCaptor.getValue()).usingRecursiveComparison()
                                                .ignoringFields("editor", "creator", "editionDate",
                                                    "creationDate", "lineRelations")
                                                .isEqualTo(versionModel);
  }

  @Test
  void shouldGetVersions() {
    // Given
    Version version = createEntity();
    when(versionRepository.findAll(any(Pageable.class))).thenReturn(
        new PageImpl<>(Collections.singletonList(version)));

    // When
    List<VersionModel> versions = versionController.getVersions(Pageable.unpaged());

    // Then
    assertThat(versions).hasSize(1)
                        .first()
                        .usingRecursiveComparison()
                        .ignoringFields("editor", "creator", "editionDate", "creationDate",
                            "lineRelations")
                        .isEqualTo(version);
  }

  @Test
  void shouldGetVersion() {
    // Given
    Version version = createEntity();
    when(versionRepository.findById(anyLong())).thenReturn(Optional.of(version));

    // When
    VersionModel versionModel = versionController.getVersion(1L);

    // Then
    assertThat(versionModel).usingRecursiveComparison()
                            .ignoringFields("editor", "creator", "editionDate", "creationDate",
                                "lineRelations")
                            .isEqualTo(versionModel);
  }

  @Test
  void shouldReturnNotFoundOnUnexistingVersion() {
    // Given
    when(versionRepository.findById(anyLong())).thenReturn(Optional.empty());

    // When

    // Then
    assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(
        () -> versionController.getVersion(1L)).withMessage(HttpStatus.NOT_FOUND.toString());
  }

  @Test
  void shouldDeleteVersion() {
    // Given
    when(versionRepository.existsById(anyLong())).thenReturn(true);

    // When
    versionController.deleteVersion(1L);

    // Then
    verify(versionRepository).deleteById(1L);
  }

  @Test
  void shouldReturnNotFoundOnDeletingUnexistingVersion() {
    // Given
    when(versionRepository.existsById(anyLong())).thenReturn(false);

    // When

    // Then
    assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(
        () -> versionController.deleteVersion(1L)).withMessage(HttpStatus.NOT_FOUND.toString());
  }

  @Test
  void shouldUpdateVersion() {
    // Given
    Version version = createEntity();
    VersionModel versionModel = createModel();
    versionModel.setName("New name");

    when(versionRepository.findById(anyLong())).thenReturn(Optional.of(version));

    // When
    VersionModel result = versionController.updateVersion(1L, versionModel);

    // Then
    assertThat(result).usingRecursiveComparison()
                      .ignoringFields("editor", "creator", "editionDate", "creationDate",
                          "lineRelations")
                      .isEqualTo(versionModel);
  }

  @Test
  void shouldReturnNotFoundOnUnexistingUpdateVersion() {
    // Given
    when(versionRepository.findById(anyLong())).thenReturn(Optional.empty());

    // When
    VersionModel versionModel = createModel();

    // Then
    assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(
                                                                () -> versionController.updateVersion(1L, versionModel))
                                                            .withMessage(
                                                                HttpStatus.NOT_FOUND.toString());
  }

  private static Version createEntity() {
    return Version.builder()
                  .ttfnid("ch:1:fpfnid:100000")
                  .name("FPFN Name")
                  .number("BEX")
                  .swissTimetableFieldNumber("b0.BEX")
                  .validFrom(LocalDate.of(2020, 12, 12))
                  .validTo(LocalDate.of(2099, 12, 12))
                  .build();
  }

  private static VersionModel createModel() {
    return VersionModel.builder()
                       .ttfnid("ch:1:fpfnid:100000")
                       .name("FPFN Name")
                       .number("BEX")
                       .swissTimetableFieldNumber("b0.BEX")
                       .validFrom(LocalDate.of(2020, 12, 12))
                       .validTo(LocalDate.of(2099, 12, 12))
                       .build();
  }
}