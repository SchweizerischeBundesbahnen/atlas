package ch.sbb.line.directory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.line.directory.api.SublineVersionModel;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.enumaration.SublineType;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class SublineVersionControllerTest {

  @Mock
  private SublineVersionRepository sublineVersionRepository;

  private SublineVersionController sublineVersionController;

  @Captor
  private ArgumentCaptor<SublineVersion> versionArgumentCaptor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    sublineVersionController = new SublineVersionController(sublineVersionRepository);
    when(sublineVersionRepository.save(any())).then(i -> i.getArgument(0, SublineVersion.class));
  }

  @Test
  public void shouldSaveNewVersion() {
    // Given
    SublineVersionModel sublineVersionModel = createModel();

    // When
    sublineVersionController.createSublineVersion(sublineVersionModel);

    // Then
    verify(sublineVersionRepository).save(versionArgumentCaptor.capture());
    assertThat(versionArgumentCaptor.getValue()).usingRecursiveComparison()
                                                .ignoringFields("editor", "creator", "editionDate",
                                                    "creationDate")
                                                .isEqualTo(sublineVersionModel);
  }

  @Test
  void shouldGetVersions() {
    // Given
    SublineVersion sublineVersion = createEntity();
    when(sublineVersionRepository.findAllBySwissLineNumber(any())).thenReturn(
        Collections.singleton(sublineVersion));
    when(sublineVersionRepository.count()).thenReturn(1L);

    // When
    Set<SublineVersionModel> versions = sublineVersionController.getSublineVersionsBySwissLineNumber(sublineVersion.getSwissLineNumber());

    // Then
    assertThat(versions).isNotNull();
    assertThat(versions).hasSize(1)
                                      .first()
                                      .usingRecursiveComparison()
                                      .ignoringFields("editor", "creator", "editionDate",
                                          "creationDate")
                                      .isEqualTo(sublineVersion);
  }

  @Test
  void shouldGetVersion() {
    // Given
    SublineVersion sublineVersion = createEntity();
    when(sublineVersionRepository.findById(anyLong())).thenReturn(Optional.of(sublineVersion));

    // When
    SublineVersionModel sublineVersionModel = sublineVersionController.getSublineVersion(1L);

    // Then
    assertThat(sublineVersionModel).usingRecursiveComparison()
                                .ignoringFields("editor", "creator", "editionDate", "creationDate")
                                .isEqualTo(sublineVersionModel);
  }

  @Test
  void shouldReturnNotFoundOnUnexcitingVersion() {
    // Given
    when(sublineVersionRepository.findById(anyLong())).thenReturn(Optional.empty());

    // When

    // Then
    assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(
                                                                () -> sublineVersionController.getSublineVersion(1L))
                                                            .withMessage(
                                                                HttpStatus.NOT_FOUND.toString());
  }

  @Test
  void shouldDeleteVersion() {
    // Given
    when(sublineVersionRepository.existsById(anyLong())).thenReturn(true);

    // When
    sublineVersionController.deleteSublineVersion(1L);

    // Then
    verify(sublineVersionRepository).deleteById(1L);
  }

  @Test
  void shouldReturnNotFoundOnDeletingUnexistingVersion() {
    // Given
    when(sublineVersionRepository.existsById(anyLong())).thenReturn(false);

    // When

    // Then
    assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(
                                                                () -> sublineVersionController.deleteSublineVersion(1L))
                                                            .withMessage(
                                                                HttpStatus.NOT_FOUND.toString());
  }

  @Test
  void shouldUpdateVersion() {
    // Given
    SublineVersion sublineVersion = createEntity();
    SublineVersionModel sublineVersionModel = createModel();
    sublineVersionModel.setShortName("New name");

    when(sublineVersionRepository.findById(anyLong())).thenReturn(Optional.of(sublineVersion));

    // When
    SublineVersionModel result = sublineVersionController.updateSublineVersion(1L, sublineVersionModel);

    // Then
    assertThat(result).usingRecursiveComparison()
                      .ignoringFields("editor", "creator", "editionDate", "creationDate")
                      .isEqualTo(sublineVersionModel);
  }

  @Test
  void shouldReturnNotFoundOnUnexistingUpdateVersion() {
    // Given
    when(sublineVersionRepository.findById(anyLong())).thenReturn(Optional.empty());

    // When
    SublineVersionModel sublineVersionModel = createModel();

    // Then
    assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(
                                                                () -> sublineVersionController.updateSublineVersion(1L,
                                                                    sublineVersionModel))
                                                            .withMessage(
                                                                HttpStatus.NOT_FOUND.toString());
  }

  private static SublineVersion createEntity() {
    return SublineVersion.builder()
                      .swissSublineNumber("swissSublineNumber")
                      .swissLineNumber("swissLineNumber")
                      .status(Status.ACTIVE)
                      .type(SublineType.TECHNICAL)
                      .slnid("slnid")
                      .description("description")
                      .shortName("shortName")
                      .longName("longName")
                      .paymentType(PaymentType.INTERNATIONAL)
                      .validFrom(LocalDate.of(2020,12,12))
                      .validTo(LocalDate.of(2099,12,12))
                      .businessOrganisation("businessOrganisation")
                      .build();
  }

  private static SublineVersionModel createModel() {
    return SublineVersionModel.builder()
                         .swissSublineNumber("swissSublineNumber")
                         .swissLineNumber("swissLineNumber")
                         .status(Status.ACTIVE)
                         .type(SublineType.TECHNICAL)
                         .slnid("slnid")
                         .description("description")
                         .shortName("shortName")
                         .longName("longName")
                         .paymentType(PaymentType.INTERNATIONAL)
                         .validFrom(LocalDate.of(2020,12,12))
                         .validTo(LocalDate.of(2099,12,12))
                         .businessOrganisation("businessOrganisation")
                         .build();
  }
}