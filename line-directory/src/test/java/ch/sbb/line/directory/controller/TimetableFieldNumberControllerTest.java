package ch.sbb.line.directory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.line.directory.api.Container;
import ch.sbb.line.directory.api.TimetableFieldNumberModel;
import ch.sbb.line.directory.api.TimetableFieldNumberVersionModel;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.exception.NotFoundException;
import ch.sbb.line.directory.service.TimetableFieldNumberService;
import java.time.LocalDate;
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

public class TimetableFieldNumberControllerTest {

  @Mock
  private TimetableFieldNumberService timetableFieldNumberService;

  @InjectMocks
  private TimetableFieldNumberController timetableFieldNumberController;

  @Captor
  private ArgumentCaptor<TimetableFieldNumberVersion> versionArgumentCaptor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(timetableFieldNumberService.save(any())).then(i -> i.getArgument(0, TimetableFieldNumberVersion.class));
  }

  @Test
  public void shouldSaveNewVersion() {
    // Given
    TimetableFieldNumberVersionModel timetableFieldNumberVersionModel = createModel();

    // When
    timetableFieldNumberController.createVersion(timetableFieldNumberVersionModel);

    // Then
    verify(timetableFieldNumberService).save(versionArgumentCaptor.capture());
    assertThat(versionArgumentCaptor.getValue()).usingRecursiveComparison()
        .ignoringFields("editor", "creator", "editionDate",
            "creationDate", "lineRelations", "ttfnid", "version")
        .isEqualTo(timetableFieldNumberVersionModel);
  }

  @Test
  void shouldGetOverview() {
    // Given
    TimetableFieldNumber version = createOverviewEntity();
    when(timetableFieldNumberService.getVersionsSearched(any(Pageable.class), any(), any(), any())).thenReturn(
        new PageImpl<>(Collections.singletonList(version)));

    // When
    Container<TimetableFieldNumberModel> timetableFieldNumberContainer = timetableFieldNumberController.getOverview(Pageable.unpaged(), null, null, null);

    // Then
    assertThat(timetableFieldNumberContainer).isNotNull();
    assertThat(timetableFieldNumberContainer.getObjects()).hasSize(1)
        .first()
        .usingRecursiveComparison()
        .isEqualTo(version);
    assertThat(timetableFieldNumberContainer.getTotalCount()).isEqualTo(1);
  }

  @Test
  void shouldDeleteVersions() {
    // Given
    String ttfnid = "ch:1:ttfnid:100000";
    TimetableFieldNumberVersion version = createEntity();
    TimetableFieldNumberVersion version2 = createEntity();
    List<TimetableFieldNumberVersion> versions = List.of(version, version2);
    when(timetableFieldNumberService.getAllVersionsVersioned(ttfnid)).thenReturn(versions);

    // When
    timetableFieldNumberController.deleteVersions(ttfnid);

    // Then
    verify(timetableFieldNumberService).deleteAll(versions);
  }

  @Test
  void shouldReturnNotFoundOnDeletingUnexistingVersion() {
    // Given
    String ttfnid = "ch:1:ttfnid:100000";
    when(
        timetableFieldNumberService.getAllVersionsVersioned(ttfnid)).thenReturn(List.of());

    // When

    // Then
    assertThatExceptionOfType(NotFoundException.class).isThrownBy(
        () -> timetableFieldNumberController.deleteVersions(ttfnid));
  }

  private static TimetableFieldNumber createOverviewEntity() {
    return TimetableFieldNumber.builder()
        .ttfnid("ch:1:ttfnid:100000")
        .description("FPFN Description")
        .swissTimetableFieldNumber("b0.BEX")
        .validFrom(LocalDate.of(2020, 12, 12))
        .validTo(LocalDate.of(2099, 12, 12))
        .build();
  }

  private static TimetableFieldNumberVersion createEntity() {
    return TimetableFieldNumberVersion.builder()
                                      .ttfnid("ch:1:ttfnid:100000")
                                      .description("FPFN Description")
                                      .number("BEX")
                                      .swissTimetableFieldNumber("b0.BEX")
                                      .validFrom(LocalDate.of(2020, 12, 12))
                                      .validTo(LocalDate.of(2099, 12, 12))
                                      .build();
  }

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
}
