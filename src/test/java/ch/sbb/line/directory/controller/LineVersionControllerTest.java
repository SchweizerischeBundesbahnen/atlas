package ch.sbb.line.directory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.line.directory.api.LineVersionModel;
import ch.sbb.line.directory.api.VersionsContainer;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.model.CmykColor;
import ch.sbb.line.directory.model.RgbColor;
import ch.sbb.line.directory.service.LineService;
import ch.sbb.line.directory.swiss.number.SwissNumberUniqueValidator;
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

public class LineVersionControllerTest {

  private static final RgbColor RGB_COLOR = new RgbColor(0, 0, 0);
  private static final CmykColor CYMK_COLOR = new CmykColor(0, 0, 0, 0);

  @Mock
  private LineService lineService;

  @Mock
  private SublineVersionController sublineVersionController;

  @Mock
  private SwissNumberUniqueValidator swissNumberUniqueValidator;

  private LineVersionController lineVersionController;

  @Captor
  private ArgumentCaptor<LineVersion> versionArgumentCaptor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    lineVersionController = new LineVersionController(lineService);
    when(lineService.save(any())).then(i -> i.getArgument(0, LineVersion.class));
  }

  @Test
  public void shouldSaveNewVersion() {
    // Given
    LineVersionModel lineVersionModel = createModel();

    // When
    lineVersionController.createLineVersion(lineVersionModel);

    // Then
    verify(lineService).save(versionArgumentCaptor.capture());
    assertThat(versionArgumentCaptor.getValue()).usingRecursiveComparison()
                                                .ignoringFields("editor", "creator", "editionDate",
                                                    "creationDate")
                                                .ignoringFieldsMatchingRegexes("color.*")
                                                .isEqualTo(lineVersionModel);
  }

  @Test
  void shouldGetVersions() {
    // Given
    LineVersion lineVersion = createEntity();
    when(lineService.findAll(any(Pageable.class))).thenReturn(
        new PageImpl<>(Collections.singletonList(lineVersion)));
    when(lineService.totalCount()).thenReturn(1L);

    // When
    VersionsContainer<LineVersionModel> lineVersionContainer = lineVersionController.getLineVersions(
        Pageable.unpaged());

    // Then
    assertThat(lineVersionContainer).isNotNull();
    assertThat(lineVersionContainer.getVersions()).hasSize(1)
                                                  .first()
                                                  .usingRecursiveComparison()
                                                  .ignoringFields("editor", "creator",
                                                      "editionDate",
                                                      "creationDate")
                                                  .ignoringFieldsMatchingRegexes("color.*")
                                                  .isEqualTo(lineVersion);
    assertThat(lineVersionContainer.getTotalCount()).isEqualTo(1);
  }

  @Test
  void shouldGetVersion() {
    // Given
    LineVersion lineVersion = createEntity();
    when(lineService.findLineById(anyLong())).thenReturn(Optional.of(lineVersion));

    // When
    LineVersionModel lineVersionModel = lineVersionController.getLineVersion(1L);

    // Then
    assertThat(lineVersionModel).usingRecursiveComparison()
                                .ignoringFields("editor", "creator", "editionDate", "creationDate")
                                .isEqualTo(lineVersionModel);
  }

  @Test
  void shouldReturnNotFoundOnUnexcitingVersion() {
    // Given
    when(lineService.findLineById(anyLong())).thenReturn(Optional.empty());

    // When

    // Then
    assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(
                                                                () -> lineVersionController.getLineVersion(1L))
                                                            .withMessage(
                                                                HttpStatus.NOT_FOUND.toString());
  }

  @Test
  void shouldDeleteVersion() {
    // Given

    // When
    lineVersionController.deleteLineVersion(1L);

    // Then
    verify(lineService).deleteLineById(1L);
  }


  @Test
  void shouldUpdateVersion() {
    // Given
    LineVersion lineVersion = createEntity();
    LineVersionModel lineVersionModel = createModel();
    lineVersionModel.setShortName("New name");

    when(lineService.findLineById(anyLong())).thenReturn(Optional.of(lineVersion));

    // When
    LineVersionModel result = lineVersionController.updateLineVersion(1L, lineVersionModel);

    // Then
    assertThat(result).usingRecursiveComparison()
                      .ignoringFields("editor", "creator", "editionDate", "creationDate")
                      .isEqualTo(lineVersionModel);
  }

  @Test
  void shouldReturnNotFoundOnUnexistingUpdateVersion() {
    // Given
    when(lineService.findLineById(anyLong())).thenReturn(Optional.empty());

    // When
    LineVersionModel lineVersionModel = createModel();

    // Then
    assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(
                                                                () -> lineVersionController.updateLineVersion(1L,
                                                                    lineVersionModel))
                                                            .withMessage(
                                                                HttpStatus.NOT_FOUND.toString());
  }

  private static LineVersion createEntity() {
    return LineVersion.builder()
                      .status(Status.ACTIVE)
                      .type(LineType.ORDERLY)
                      .slnid("slnid")
                      .paymentType(PaymentType.INTERNATIONAL)
                      .shortName("shortName")
                      .alternativeName("alternativeName")
                      .combinationName("combinationName")
                      .longName("longName")
                      .colorFontRgb(RGB_COLOR)
                      .colorBackRgb(RGB_COLOR)
                      .colorFontCmyk(CYMK_COLOR)
                      .colorBackCmyk(CYMK_COLOR)
                      .description("description")
                      .validFrom(LocalDate.of(2020, 12, 12))
                      .validTo(LocalDate.of(2099, 12, 12))
                      .businessOrganisation("businessOrganisation")
                      .comment("comment")
                      .swissLineNumber("swissLineNumber")
                      .build();
  }

  private static LineVersionModel createModel() {
    return LineVersionModel.builder()
                           .status(Status.ACTIVE)
                           .type(LineType.ORDERLY)
                           .slnid("slnid")
                           .paymentType(PaymentType.INTERNATIONAL)
                           .shortName("shortName")
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
}