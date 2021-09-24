package ch.sbb.line.directory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.line.directory.api.VersionModel;
import ch.sbb.line.directory.api.VersionsContainer;
import ch.sbb.line.directory.entity.Version;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.repository.VersionRepository;
import java.awt.Color;
import java.time.LocalDate;
import java.util.Collections;
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

public class VersionControllerTest {

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
  public void shouldSaveNewVersion() {
    // Given
    VersionModel versionModel = createModel();

    // When
    versionController.createVersion(versionModel);

    // Then
    verify(versionRepository).save(versionArgumentCaptor.capture());
    assertThat(versionArgumentCaptor.getValue()).usingRecursiveComparison()
                                                .ignoringFields("editor", "creator", "editionDate",
                                                    "creationDate", "sublineVersions")
                                                .ignoringFieldsMatchingRegexes("color.*")
                                                .isEqualTo(versionModel);
  }

  @Test
  void shouldGetVersions() {
    // Given
    Version version = createEntity();
    when(versionRepository.findAll(any(Pageable.class))).thenReturn(
        new PageImpl<>(Collections.singletonList(version)));
    when(versionRepository.count()).thenReturn(1L);

    // When
    VersionsContainer versions = versionController.getVersions(Pageable.unpaged());

    // Then
    assertThat(versions).isNotNull();
    assertThat(versions.getVersions()).hasSize(1)
                                      .first()
                                      .usingRecursiveComparison()
                                      .ignoringFields("editor", "creator", "editionDate",
                                          "creationDate",
                                          "sublineVersions")
                                      .ignoringFieldsMatchingRegexes("color.*")
                                      .isEqualTo(version);
    assertThat(versions.getTotalCount()).isEqualTo(1);
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
                                "sublineVersions")
                            .isEqualTo(versionModel);
  }

  @Test
  void shouldReturnNotFoundOnUnexcitingVersion() {
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
    versionModel.setShortName("New name");

    when(versionRepository.findById(anyLong())).thenReturn(Optional.of(version));

    // When
    VersionModel result = versionController.updateVersion(1L, versionModel);

    // Then
    assertThat(result).usingRecursiveComparison()
                      .ignoringFields("editor", "creator", "editionDate", "creationDate",
                          "sublineVersions")
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
                  .sublineVersions(Collections.emptySet())
                  .status(Status.ACTIVE)
                  .type(LineType.ORDERLY)
                  .slnid("slnid")
                  .paymentType(PaymentType.INTERNATIONAL)
                  .shortName("shortName")
                  .alternativeName("alternativeName")
                  .combinationName("combinationName")
                  .longName("longName")
                  .colorFontRgb(Color.black)
                  .colorBackRgb(Color.black)
                  .colorFontCmyk(Color.black)
                  .colorBackCmyk(Color.black)
                  .description("description")
                  .validFrom(LocalDate.of(2020, 12, 12))
                  .validTo(LocalDate.of(2099, 12, 12))
                  .businessOrganisation("businessOrganisation")
                  .comment("comment")
                  .swissLineNumber("swissLineNumber")
                  .build();
  }

  private static VersionModel createModel() {
    return VersionModel.builder()
                       .sublineVersions(Collections.emptySet())
                       .status(Status.ACTIVE)
                       .type(LineType.ORDERLY)
                       .slnid("slnid")
                       .paymentType(PaymentType.INTERNATIONAL)
                       .shortName("shortName")
                       .alternativeName("alternativeName")
                       .combinationName("combinationName")
                       .longName("longName")
                       .colorFontRgb("#ffffff")
                       .colorBackRgb("#ffffff")
                       .colorFontCmyk("#ffffff")
                       .colorBackCmyk("#ffffff")
                       .description("description")
                       .validFrom(LocalDate.of(2020, 12, 12))
                       .validTo(LocalDate.of(2099, 12, 12))
                       .businessOrganisation("businessOrganisation")
                       .comment("comment")
                       .swissLineNumber("swissLineNumber")
                       .build();
  }
}