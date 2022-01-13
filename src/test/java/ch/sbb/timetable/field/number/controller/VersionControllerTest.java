package ch.sbb.timetable.field.number.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.timetable.field.number.api.TimetableFieldNumberContainer;
import ch.sbb.timetable.field.number.api.VersionModel;
import ch.sbb.timetable.field.number.entity.TimetableFieldNumber;
import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.service.VersionService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class VersionControllerTest {

  @Mock
  private VersionService versionService;

  @InjectMocks
  private VersionController versionController;

  @Captor
  private ArgumentCaptor<Version> versionArgumentCaptor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(versionService.save(any())).then(i -> i.getArgument(0, Version.class));
  }

  @Test
  public void shouldSaveNewVersion() {
    // Given
    VersionModel versionModel = createModel();

    // When
    versionController.createVersion(versionModel);

    // Then
    verify(versionService).save(versionArgumentCaptor.capture());
    assertThat(versionArgumentCaptor.getValue()).usingRecursiveComparison()
        .ignoringFields("editor", "creator", "editionDate",
            "creationDate", "lineRelations", "ttfnid", "version")
        .isEqualTo(versionModel);
  }

  @Test
  void shouldGetOverview() {
    // Given
    TimetableFieldNumber version = createOverviewEntity();
    when(versionService.getVersionsSearched(any(Pageable.class), any(), any(), any())).thenReturn(
        new PageImpl<>(Collections.singletonList(version)));

    // When
    TimetableFieldNumberContainer timetableFieldNumberContainer = versionController.getOverview(Pageable.unpaged(), null, null, null);

    // Then
    assertThat(timetableFieldNumberContainer).isNotNull();
    assertThat(timetableFieldNumberContainer.getFieldNumbers()).hasSize(1)
        .first()
        .usingRecursiveComparison()
        .isEqualTo(version);
    assertThat(timetableFieldNumberContainer.getTotalCount()).isEqualTo(1);
  }

  @Test
  void shouldGetVersion() {
    // Given
    Version version = createEntity();
    when(versionService.findById(anyLong())).thenReturn(Optional.of(version));

    // When
    VersionModel versionModel = versionController.getVersion(1L);

    // Then
    assertThat(versionModel).usingRecursiveComparison()
        .ignoringFields("editor", "creator", "editionDate", "creationDate",
            "lineRelations")
        .isEqualTo(versionModel);
  }

  @Test
  void shouldReturnNotFoundOnUnexcitingVersion() {
    // Given
    when(versionService.findById(anyLong())).thenReturn(Optional.empty());

    // When

    // Then
    assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(
        () -> versionController.getVersion(1L)).withMessage(HttpStatus.NOT_FOUND.toString());
  }

  @Test
  void shouldDeleteVersions() {
    // Given
    String ttfnid = "ch:1:ttfnid:100000";
    Version version = createEntity();
    Version version2 = createEntity();
    List<Version> versions = List.of(version, version2);
    when(versionService.getAllVersionsVersioned(ttfnid)).thenReturn(versions);

    // When
    versionController.deleteVersions(ttfnid);

    // Then
    verify(versionService).deleteAll(versions);
  }

  @Test
  void shouldReturnNotFoundOnDeletingUnexistingVersion() {
    // Given
    String ttfnid = "ch:1:ttfnid:100000";
    when(
        versionService.getAllVersionsVersioned(ttfnid)).thenReturn(List.of());

    // When

    // Then
    assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(
        () -> versionController.deleteVersions(ttfnid)).withMessage(HttpStatus.NOT_FOUND.toString());
  }

  @Test
  void shouldUpdateVersion() {
    // Given
    Version version = createEntity();
    VersionModel versionModel = createModel();
    versionModel.setName("New name");

    when(versionService.findById(anyLong())).thenReturn(Optional.of(version));

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
    when(versionService.findById(anyLong())).thenReturn(Optional.empty());

    // When
    VersionModel versionModel = createModel();

    // Then
    assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(
            () -> versionController.updateVersion(1L, versionModel))
        .withMessage(
            HttpStatus.NOT_FOUND.toString());
  }

  @Test
  void shouldThrowConflictException() {
    // Given
    List<Version> overlappingVersions = new ArrayList<>();
    VersionModel versionModel = createModel();
    Version versionEntity = createEntity();
    overlappingVersions.add(versionEntity);
    doReturn(overlappingVersions).when(versionService).getOverlapsOnNumberAndSttfn(any());
//    when(versionService.getOverlapsOnNumberAndSttfn(versionEntity)).thenReturn(overlappingVersions);

    // When
    VersionModel result = versionController.createVersion(versionModel);

    // Then
    assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(
            () -> versionController.updateVersion(1L, versionModel))
        .withMessage(
            HttpStatus.NOT_FOUND.toString());
  }

  private static TimetableFieldNumber createOverviewEntity() {
    return TimetableFieldNumber.builder()
        .ttfnid("ch:1:ttfnid:100000")
        .name("FPFN Name")
        .swissTimetableFieldNumber("b0.BEX")
        .validFrom(LocalDate.of(2020, 12, 12))
        .validTo(LocalDate.of(2099, 12, 12))
        .build();
  }

  private static Version createEntity() {
    return Version.builder()
        .ttfnid("ch:1:ttfnid:100000")
        .name("FPFN Name")
        .number("BEX")
        .swissTimetableFieldNumber("b0.BEX")
        .validFrom(LocalDate.of(2020, 12, 12))
        .validTo(LocalDate.of(2099, 12, 12))
        .build();
  }

  private static VersionModel createModel() {
    return VersionModel.builder()
        .ttfnid("ch:1:ttfnid:100000")
        .name("FPFN Name")
        .number("BEX")
        .swissTimetableFieldNumber("b0.BEX")
        .validFrom(LocalDate.of(2020, 12, 12))
        .validTo(LocalDate.of(2099, 12, 12))
        .build();
  }
}
