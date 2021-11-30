package ch.sbb.line.directory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.api.Container;
import ch.sbb.line.directory.api.SublineModel;
import ch.sbb.line.directory.api.SublineVersionModel;
import ch.sbb.line.directory.entity.Subline;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.enumaration.SublineType;
import ch.sbb.line.directory.service.SublineService;
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

public class SublineControllerTest {

  @Mock
  private SublineService sublineService;

  private SublineController sublineController;

  @Captor
  private ArgumentCaptor<SublineVersion> versionArgumentCaptor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    sublineController = new SublineController(sublineService);
    when(sublineService.save(any())).then(i -> i.getArgument(0, SublineVersion.class));
  }

  @Test
  void shouldGetSublines() {
    // Given
    Subline subline = SublineTestData.subline();
    when(sublineService.findAll(any(Pageable.class))).thenReturn(
        new PageImpl<>(Collections.singletonList(subline)));

    // When
    Container<SublineModel> sublineContainer = sublineController.getSublines(
        Pageable.unpaged());

    // Then
    assertThat(sublineContainer).isNotNull();
    assertThat(sublineContainer.getObjects()).hasSize(1)
                                             .first()
                                             .usingRecursiveComparison()
                                             .isEqualTo(subline);
    assertThat(sublineContainer.getTotalCount()).isEqualTo(1);
  }

  @Test
  void shouldGetSubline() {
    // Given
    SublineVersion sublineVersion = SublineTestData.sublineVersion();
    when(sublineService.findSubline(any())).thenReturn(Collections.singletonList(sublineVersion));

    // When
    List<SublineVersionModel> subline = sublineController.getSubline("slnid");

    // Then
    assertThat(subline).isNotNull();
    assertThat(subline).hasSize(1)
                       .first()
                       .usingRecursiveComparison()
                       .isEqualTo(sublineVersion);
  }

  @Test
  public void shouldSaveNewVersion() {
    // Given
    SublineVersionModel sublineVersionModel = createModel();

    // When
    sublineController.createSublineVersion(sublineVersionModel);

    // Then
    verify(sublineService).save(versionArgumentCaptor.capture());
    assertThat(versionArgumentCaptor.getValue()).usingRecursiveComparison()
                                                .ignoringFields("editor", "creator", "editionDate",
                                                    "creationDate", "version")
                                                .isEqualTo(sublineVersionModel);
  }


  @Test
  void shouldGetVersion() {
    // Given
    SublineVersion sublineVersion = SublineTestData.sublineVersion();
    when(sublineService.findById(anyLong())).thenReturn(Optional.of(sublineVersion));

    // When
    SublineVersionModel sublineVersionModel = sublineController.getSublineVersion(1L);

    // Then
    assertThat(sublineVersionModel).usingRecursiveComparison()
                                .ignoringFields("editor", "creator", "editionDate", "creationDate")
                                .isEqualTo(sublineVersionModel);
  }

  @Test
  void shouldReturnNotFoundOnUnexcitingVersion() {
    // Given
    when(sublineService.findById(anyLong())).thenReturn(Optional.empty());

    // When

    // Then
    assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(
                                                                () -> sublineController.getSublineVersion(1L))
                                                            .withMessage(
                                                                HttpStatus.NOT_FOUND.toString());
  }

  @Test
  void shouldDeleteVersion() {
    // Given

    // When
    sublineController.deleteSublineVersion(1L);

    // Then
    verify(sublineService).deleteById(1L);
  }


  @Test
  void shouldUpdateVersion() {
    // Given
    SublineVersion sublineVersion = SublineTestData.sublineVersion();
    SublineVersionModel sublineVersionModel = createModel();
    sublineVersionModel.setNumber("New name");

    when(sublineService.findById(anyLong())).thenReturn(Optional.of(sublineVersion));

    // When
    SublineVersionModel result = sublineController.updateSublineVersion(1L, sublineVersionModel);

    // Then
    assertThat(result).usingRecursiveComparison()
                      .ignoringFields("editor", "creator", "editionDate", "creationDate")
                      .isEqualTo(sublineVersionModel);
  }

  @Test
  void shouldUpdateVersionWithVersioning() {
    // Given
    SublineVersion sublineVersion = SublineTestData.sublineVersion();
    SublineVersionModel sublineVersionModel = createModel();
    sublineVersionModel.setNumber("New name");

    when(sublineService.findById(anyLong())).thenReturn(Optional.of(sublineVersion));

    // When
    sublineController.updateWithVersioning(1L, sublineVersionModel);

    // Then
    verify(sublineService).updateVersion(any(), any());
  }

  @Test
  void shouldReturnNotFoundOnUnexistingUpdateVersion() {
    // Given
    when(sublineService.findById(anyLong())).thenReturn(Optional.empty());

    // When
    SublineVersionModel sublineVersionModel = createModel();

    // Then
    assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(
                                                                () -> sublineController.updateSublineVersion(1L,
                                                                    sublineVersionModel))
                                                            .withMessage(
                                                                HttpStatus.NOT_FOUND.toString());
  }

  private static SublineVersionModel createModel() {
    return SublineVersionModel.builder()
                         .swissSublineNumber("swissSublineNumber")
                         .swissLineNumber("swissLineNumber")
                         .status(Status.ACTIVE)
                         .type(SublineType.TECHNICAL)
                         .slnid("slnid")
                         .description("description")
                         .number("number")
                         .longName("longName")
                         .paymentType(PaymentType.INTERNATIONAL)
                         .validFrom(LocalDate.of(2020,12,12))
                         .validTo(LocalDate.of(2099,12,12))
                         .businessOrganisation("businessOrganisation")
                         .build();
  }
}