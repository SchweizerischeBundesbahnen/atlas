package ch.sbb.line.directory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.lidi.SublineModel;
import ch.sbb.atlas.api.lidi.SublineVersionModel;
import ch.sbb.atlas.api.lidi.enumaration.PaymentType;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.SublineTestData;
import ch.sbb.line.directory.entity.Subline;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.service.CoverageService;
import ch.sbb.line.directory.service.SublineService;
import ch.sbb.line.directory.service.export.SublineVersionExportService;
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

 class SublineControllerTest {

  private static final String[] RECURSIVE_COMPARISION_IGNORE_FIELDS = {"editor", "creator",
      "editionDate", "creationDate", "version", "etagVersion"};

  @Mock
  private SublineService sublineService;

  @Mock
  private CoverageService coverageService;

  @Mock
  private SublineVersionExportService sublineVersionExportService;

  private SublineController sublineController;

  @Captor
  private ArgumentCaptor<SublineVersion> versionArgumentCaptor;

  private static SublineVersionModel createModel() {
    return SublineVersionModel.builder()
        .swissSublineNumber("swissSublineNumber")
        .mainlineSlnid("mainlineSlnid")
        .status(Status.VALIDATED)
        .sublineType(SublineType.TECHNICAL)
        .slnid("slnid")
        .description("description")
        .number("number")
        .longName("longName")
        .paymentType(PaymentType.INTERNATIONAL)
        .validFrom(LocalDate.of(2020, 12, 12))
        .validTo(LocalDate.of(2099, 12, 12))
        .businessOrganisation("businessOrganisation")
        .build();
  }

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    sublineController = new SublineController(sublineService, coverageService,
        sublineVersionExportService);
    when(sublineService.create(any())).then(i -> i.getArgument(0, SublineVersion.class));
  }

  @Test
  void shouldGetSublines() {
    // Given
    Subline subline = SublineTestData.subline();
    when(sublineService.findAll(any())).thenReturn(
        new PageImpl<>(Collections.singletonList(subline)));

    // When
    Container<SublineModel> sublineContainer = sublineController.getSublines(
        Pageable.unpaged(), Collections.emptyList(), Collections.emptyList(),
        Collections.emptyList(), Optional.empty(), Optional.of(LocalDate.now()));

    // Then
    assertThat(sublineContainer).isNotNull();
    assertThat(sublineContainer.getObjects()).hasSize(1)
        .first()
        .usingRecursiveComparison()
        .ignoringFields(RECURSIVE_COMPARISION_IGNORE_FIELDS)
        .isEqualTo(subline);
    assertThat(sublineContainer.getTotalCount()).isEqualTo(1);
  }

  @Test
  void shouldGetSubline() {
    // Given
    SublineVersion sublineVersion = SublineTestData.sublineVersion();
    when(sublineService.findSubline(any())).thenReturn(Collections.singletonList(sublineVersion));

    // When
    List<SublineVersionModel> subline = sublineController.getSublineVersion("slnid");

    // Then
    assertThat(subline).isNotNull();
    assertThat(subline).hasSize(1)
        .first()
        .usingRecursiveComparison()
        .ignoringFields(RECURSIVE_COMPARISION_IGNORE_FIELDS)
        .isEqualTo(sublineVersion);
  }

  @Test
   void shouldSaveNewVersion() {
    // Given
    SublineVersionModel sublineVersionModel = createModel();

    // When
    sublineController.createSublineVersion(sublineVersionModel);

    // Then
    verify(sublineService).create(versionArgumentCaptor.capture());
    assertThat(versionArgumentCaptor.getValue()).usingRecursiveComparison()
        .ignoringFields(RECURSIVE_COMPARISION_IGNORE_FIELDS)
        .isEqualTo(sublineVersionModel);
  }

  @Test
  void shouldDeleteVersion() {
    // Given
    String slnid = "ch:1:slnid:10000";
    // When
    sublineController.deleteSublines(slnid);

    // Then
    verify(sublineService).deleteAll(slnid);
  }

  @Test
  void shouldUpdateVersionWithVersioning() {
    // Given
    SublineVersion sublineVersion = SublineTestData.sublineVersion();
    SublineVersionModel sublineVersionModel = createModel();
    sublineVersionModel.setNumber("New name");

    when(sublineService.findById(anyLong())).thenReturn(Optional.of(sublineVersion));

    // When
    sublineController.updateSublineVersion(1L, sublineVersionModel);

    // Then
    verify(sublineService).update(any(), any(), any());
  }
}
