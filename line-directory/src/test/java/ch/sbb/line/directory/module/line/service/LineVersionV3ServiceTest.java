package ch.sbb.line.directory.module.line.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.lidi.LineVersionModelV3;
import ch.sbb.atlas.api.lidi.enumaration.v3.ConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineObjectType;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.exception.RevokedException;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.repository.LineVersionV3Repository;
import ch.sbb.line.directory.module.line.search.LineVersionV3SearchRestrictions;
import ch.sbb.line.directory.module.line.validation.LineVersionV3ValidationService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

class LineVersionV3ServiceTest {

  private static final int SUBLINE_ROW_COUNT = 5;
  private static final String GENERATED_SLNID = "ch:1:slnid:1100001";

  @Mock
  private LineVersionV3Repository lineVersionV3Repository;

  @Mock
  private VersionableService versionableService;

  @Mock
  private LineVersionV3ValidationService lineVersionV3ValidationService;

  @Mock
  private LineV3ShorteningService lineV3ShorteningService;

  private LineVersionV3Service lineVersionV3Service;

  private AutoCloseable mocks;

  @BeforeEach
  void setUp() {
    mocks = MockitoAnnotations.openMocks(this);
    lineVersionV3Service = new LineVersionV3Service(lineVersionV3Repository, versionableService,
        lineVersionV3ValidationService, lineV3ShorteningService);
  }

  @AfterEach
  void tearDown() throws Exception {
    mocks.close();
  }

  @Test
  void shouldResolveParentsWithOneBatchQueryPerPageAndNotPerRow() {
    //given
    List<LineVersionV3> sublines = IntStream.range(0, SUBLINE_ROW_COUNT)
        .mapToObj(index -> LineVersionV3TestData.sublineBuilder()
            .slnid(LineVersionV3TestData.CHILD_SLNID + index)
            .referencedLineSlnid("ch:1:slnid:parent" + index)
            .build())
        .map(LineVersionV3.class::cast)
        .toList();
    when(lineVersionV3Repository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(sublines, PageRequest.of(0, SUBLINE_ROW_COUNT), SUBLINE_ROW_COUNT));
    when(lineVersionV3Repository.findAllBySlnidIn(anySet())).thenReturn(List.of());

    //when
    lineVersionV3Service.findAll(searchRestrictions(PageRequest.of(0, SUBLINE_ROW_COUNT)));

    //then
    verify(lineVersionV3Repository, times(1)).findAllBySlnidIn(anySet());
    ArgumentCaptor<Set<String>> captor = ArgumentCaptor.captor();
    verify(lineVersionV3Repository).findAllBySlnidIn(captor.capture());
    assertThat(captor.getValue()).hasSize(SUBLINE_ROW_COUNT);
  }

  @Test
  void shouldNotQueryParentsWhenPageHoldsOnlyLines() {
    //given
    when(lineVersionV3Repository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(LineVersionV3TestData.line()), PageRequest.of(0, 20), 1));

    //when
    Page<LineVersionModelV3> result = lineVersionV3Service.findAll(searchRestrictions(PageRequest.of(0, 20)));

    //then
    verify(lineVersionV3Repository, never()).findAllBySlnidIn(anySet());
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().getFirst().getType()).isEqualTo(LineObjectType.LINE);
  }

  @Test
  void shouldSelectParentVersionValidToday() {
    //given
    LineVersionV3 child = LineVersionV3TestData.sublineBuilder()
        .validFrom(LocalDate.now().plusYears(4))
        .validTo(LocalDate.now().plusYears(5))
        .build();
    LineVersionV3 pastParent = parent("past", LocalDate.now().minusYears(5), LocalDate.now().minusYears(1));
    LineVersionV3 currentParent = parent("current", LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
    LineVersionV3 futureParent = parent("future", LocalDate.now().plusYears(1), LocalDate.now().plusYears(2));
    mockChildWithParents(child, List.of(pastParent, currentParent, futureParent));

    //when
    List<LineVersionModelV3> result = lineVersionV3Service.findBySlnid(LineVersionV3TestData.CHILD_SLNID);

    //then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getReferencedLine().getNumber()).isEqualTo("current");
  }

  @Test
  void shouldFallBackToNextFutureParentWhenNoneIsValidToday() {
    //given
    LineVersionV3 child = LineVersionV3TestData.subline();
    LineVersionV3 pastParent = parent("past", LocalDate.now().minusYears(5), LocalDate.now().minusYears(1));
    LineVersionV3 nextParent = parent("next", LocalDate.now().plusYears(1), LocalDate.now().plusYears(2));
    LineVersionV3 laterParent = parent("later", LocalDate.now().plusYears(3), LocalDate.now().plusYears(4));
    mockChildWithParents(child, List.of(laterParent, pastParent, nextParent));

    //when
    List<LineVersionModelV3> result = lineVersionV3Service.findBySlnid(LineVersionV3TestData.CHILD_SLNID);

    //then
    assertThat(result.getFirst().getReferencedLine().getNumber()).isEqualTo("next");
  }

  @Test
  void shouldFallBackToLatestParentWhenAllAreInThePast() {
    //given
    LineVersionV3 child = LineVersionV3TestData.subline();
    LineVersionV3 olderParent = parent("older", LocalDate.now().minusYears(9), LocalDate.now().minusYears(8));
    LineVersionV3 latestParent = parent("latest", LocalDate.now().minusYears(3), LocalDate.now().minusYears(2));
    mockChildWithParents(child, List.of(latestParent, olderParent));

    //when
    List<LineVersionModelV3> result = lineVersionV3Service.findBySlnid(LineVersionV3TestData.CHILD_SLNID);

    //then
    assertThat(result.getFirst().getReferencedLine().getNumber()).isEqualTo("latest");
  }

  @Test
  void shouldInheritConcessionTypeOfThePrioritizedParentVersion() {
    //given
    LineVersionV3 child = LineVersionV3TestData.subline();
    LineVersionV3 pastParent = LineVersionV3TestData.lineBuilder()
        .concessionType(ConcessionType.STATE_TREATY)
        .validFrom(LocalDate.now().minusYears(5))
        .validTo(LocalDate.now().minusYears(1))
        .build();
    LineVersionV3 currentParent = LineVersionV3TestData.lineBuilder()
        .concessionType(ConcessionType.FEDERALLY_LICENSED_LINE)
        .validFrom(LocalDate.now().minusDays(1))
        .validTo(LocalDate.now().plusDays(1))
        .build();
    mockChildWithParents(child, List.of(pastParent, currentParent));

    //when
    List<LineVersionModelV3> result = lineVersionV3Service.findBySlnid(LineVersionV3TestData.CHILD_SLNID);

    //then
    assertThat(result.getFirst().getConcessionType()).isEqualTo(ConcessionType.FEDERALLY_LICENSED_LINE);
    assertThat(result.getFirst().getReferencedLine()).hasOnlyFields("number", "lineNumber", "swissLineNumber", "lineSlnid");
  }

  @Test
  void shouldResolveRevokedParentWithoutStatusFilter() {
    //given
    LineVersionV3 child = LineVersionV3TestData.subline();
    LineVersionV3 revokedParent = LineVersionV3TestData.lineBuilder().status(Status.REVOKED).build();
    when(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(LineVersionV3TestData.CHILD_SLNID))
        .thenReturn(List.of(child));
    when(lineVersionV3Repository.findAllBySlnidIn(anySet())).thenReturn(List.of(revokedParent));

    //when
    List<LineVersionModelV3> result = lineVersionV3Service.findBySlnid(LineVersionV3TestData.CHILD_SLNID);

    //then
    assertThat(result.getFirst().getReferencedLine()).isNotNull();
    assertThat(result.getFirst().getReferencedLine().getLineSlnid()).isEqualTo(LineVersionV3TestData.PARENT_SLNID);
  }

  @Test
  void shouldReturnNullReferencedLineWhenParentRowIsAbsent() {
    //given
    LineVersionV3 child = LineVersionV3TestData.subline();
    when(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(LineVersionV3TestData.CHILD_SLNID))
        .thenReturn(List.of(child));
    when(lineVersionV3Repository.findAllBySlnidIn(anySet())).thenReturn(List.of());

    //when
    List<LineVersionModelV3> result = lineVersionV3Service.findBySlnid(LineVersionV3TestData.CHILD_SLNID);

    //then
    assertThat(result.getFirst().getType()).isEqualTo(LineObjectType.SUBLINE);
    assertThat(result.getFirst().getReferencedLine()).isNull();
  }

  @Test
  void shouldSetStatusValidatedWhenCreatingLine() {
    // Given
    LineVersionV3 newLine = newLine().status(Status.DRAFT).build();
    mockSaveGeneratingSlnid();

    // When
    LineVersionV3 result = lineVersionV3Service.create(newLine);

    // Then
    assertThat(result.getStatus()).isEqualTo(Status.VALIDATED);
    assertThat(result.getSlnid()).isEqualTo(GENERATED_SLNID);
  }

  @Test
  void shouldValidatePreconditionsBeforeSaveAndAfterVersioningRulesOnAllVersionsAfterSaveWhenCreating() {
    // Given
    LineVersionV3 newLine = newLine().build();
    mockSaveGeneratingSlnid();
    List<LineVersionV3> savedVersions = List.of(LineVersionV3TestData.lineBuilder().slnid(GENERATED_SLNID).build());
    when(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(GENERATED_SLNID)).thenReturn(savedVersions);

    // When
    lineVersionV3Service.create(newLine);

    // Then
    InOrder inOrder = inOrder(lineVersionV3ValidationService, lineVersionV3Repository);
    inOrder.verify(lineVersionV3ValidationService).validatePreconditionBusinessRules(newLine);
    inOrder.verify(lineVersionV3Repository).saveAndFlush(newLine);
    inOrder.verify(lineVersionV3ValidationService).validateValidityConstraints(savedVersions);
  }

  @Test
  void shouldNotSaveWhenPreconditionValidationFailsOnCreate() {
    // Given
    LineVersionV3 newLine = newLine().build();
    doThrow(new RevokedException("ch:1:slnid:parent")).when(lineVersionV3ValidationService)
        .validatePreconditionBusinessRules(newLine);

    // When / Then
    assertThatThrownBy(() -> lineVersionV3Service.create(newLine)).isInstanceOf(RevokedException.class);
    verify(lineVersionV3Repository, never()).saveAndFlush(any());
  }

  @Test
  void shouldPersistMemberWithoutParentResolvedFieldsWhenCreatingSubline() {
    // Given
    LineVersionV3 newMember = LineVersionV3TestData.sublineBuilder()
        .slnid(null)
        .number(null)
        .shortNumber(null)
        .offerCategory(null)
        .build();
    mockSaveGeneratingSlnid();

    // When
    lineVersionV3Service.create(newMember);

    // Then
    ArgumentCaptor<LineVersionV3> captor = ArgumentCaptor.captor();
    verify(lineVersionV3Repository).saveAndFlush(captor.capture());
    assertThat(captor.getValue())
        .extracting(LineVersionV3::getReferencedLineSlnid, LineVersionV3::getNumber, LineVersionV3::getShortNumber,
            LineVersionV3::getOfferCategory)
        .containsExactly(LineVersionV3TestData.PARENT_SLNID, null, null, null);
  }

  @Test
  void shouldCopyImmutableFieldsFromCurrentVersionWhenUpdating() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.sublineBuilder().id(4L).version(3).build();
    LineVersionV3 edited = LineVersionV3TestData.sublineBuilder()
        .slnid("ch:1:slnid:other")
        .referencedLineSlnid("ch:1:slnid:otherParent")
        .lineType(LineType.DISPOSITION)
        .description("changed")
        .version(3)
        .build();

    // When
    lineVersionV3Service.update(current, edited, List.of(current));

    // Then
    ArgumentCaptor<LineVersionV3> captor = ArgumentCaptor.captor();
    verify(versionableService).versioningObjectsDeletingNullProperties(eq(current), captor.capture(), anyList());
    assertThat(captor.getValue())
        .extracting(LineVersionV3::getSlnid, LineVersionV3::getReferencedLineSlnid, LineVersionV3::getLineType,
            LineVersionV3::getDescription)
        .containsExactly(LineVersionV3TestData.CHILD_SLNID, LineVersionV3TestData.PARENT_SLNID, LineType.TECHNICAL, "changed");
  }

  @Test
  void shouldApplyVersioningWithoutGapsAndReturnAllVersionsAfterVersioningWhenUpdating() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.lineBuilder().id(1L).version(0).build();
    LineVersionV3 edited = LineVersionV3TestData.lineBuilder().description("changed").version(0).build();
    List<LineVersionV3> currentVersions = List.of(current);
    List<VersionedObject> versionedObjects = List.of(VersionedObject.builder().build());
    List<LineVersionV3> versionsAfterVersioning = List.of(LineVersionV3TestData.lineBuilder().id(1L).build(),
        LineVersionV3TestData.lineBuilder().id(2L).build());
    when(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(LineVersionV3TestData.PARENT_SLNID))
        .thenReturn(currentVersions, versionsAfterVersioning);
    when(versionableService.versioningObjectsDeletingNullProperties(current, edited, currentVersions))
        .thenReturn(versionedObjects);

    // When
    List<LineVersionV3> result = lineVersionV3Service.update(current, edited, currentVersions);

    // Then
    InOrder inOrder = inOrder(lineVersionV3Repository, versionableService, lineVersionV3ValidationService);
    inOrder.verify(lineVersionV3Repository).incrementVersion(LineVersionV3TestData.PARENT_SLNID);
    inOrder.verify(versionableService).doNotAllowGaps(versionedObjects);
    inOrder.verify(versionableService).applyVersioning(eq(LineVersionV3.class), eq(versionedObjects), any(), any());
    inOrder.verify(lineVersionV3ValidationService).validateValidityConstraints(versionsAfterVersioning);
    assertThat(result).isEqualTo(versionsAfterVersioning);
  }

  @Test
  void shouldValidateMembersWithinParentValidityAfterVersioningWhenUpdatingLine() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.lineBuilder().id(1L).version(0).build();
    LineVersionV3 edited = LineVersionV3TestData.lineBuilder().description("changed").version(0).build();
    List<LineVersionV3> versionsAfterVersioning = List.of(LineVersionV3TestData.lineBuilder().id(1L).build());
    when(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(LineVersionV3TestData.PARENT_SLNID))
        .thenReturn(List.of(current), versionsAfterVersioning);

    // When
    lineVersionV3Service.update(current, edited, List.of(current));

    // Then
    InOrder inOrder = inOrder(versionableService, lineVersionV3ValidationService);
    inOrder.verify(versionableService).applyVersioning(eq(LineVersionV3.class), anyList(), any(), any());
    inOrder.verify(lineVersionV3ValidationService).validateMembersWithinParentValidity(versionsAfterVersioning);
  }

  @Test
  void shouldValidateMembersWithinParentValidityAfterShorteningMembersWhenUpdatingLine() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.lineBuilder().id(1L).version(0).build();
    LineVersionV3 edited = LineVersionV3TestData.lineBuilder().validTo(LocalDate.of(2030, 12, 31)).version(0).build();
    LineVersionV3 memberCurrent = LineVersionV3TestData.sublineBuilder().id(2L).version(5).build();
    LineVersionV3 memberEdited = LineVersionV3TestData.sublineBuilder().validTo(LocalDate.of(2030, 12, 31)).version(5)
        .build();
    when(lineV3ShorteningService.isOnlyValidityChanged(current, edited)).thenReturn(true);
    when(lineV3ShorteningService.isShortening(current, edited)).thenReturn(true);
    when(lineV3ShorteningService.checkAndPrepareToShortMembers(current, edited))
        .thenReturn(List.of(new LineV3MemberShortening(memberCurrent, memberEdited)));

    // When
    lineVersionV3Service.update(current, edited, List.of(current));

    // Then
    InOrder inOrder = inOrder(versionableService, lineVersionV3ValidationService);
    inOrder.verify(versionableService).versioningObjectsDeletingNullProperties(eq(memberCurrent), eq(memberEdited), anyList());
    inOrder.verify(versionableService).versioningObjectsDeletingNullProperties(eq(current), eq(edited), anyList());
    inOrder.verify(lineVersionV3ValidationService).validateMembersWithinParentValidity(anyList());
  }

  @Test
  void shouldNotValidateMembersWithinParentValidityWhenUpdatingMember() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.sublineBuilder().id(4L).version(3).build();
    LineVersionV3 edited = LineVersionV3TestData.sublineBuilder().description("changed").version(3).build();

    // When
    lineVersionV3Service.update(current, edited, List.of(current));

    // Then
    verify(lineVersionV3ValidationService, never()).validateMembersWithinParentValidity(anyList());
  }

  @Test
  void shouldThrowStaleObjectStateExceptionWhenEtagVersionIsOutdated() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.lineBuilder().id(1L).version(2).build();
    LineVersionV3 edited = LineVersionV3TestData.lineBuilder().description("changed").version(1).build();

    // When / Then
    assertThatThrownBy(() -> lineVersionV3Service.update(current, edited, List.of(current)))
        .isInstanceOf(StaleObjectStateException.class);
    verify(versionableService, never()).applyVersioning(any(), anyList(), any(), any());
  }

  @Test
  void shouldRejectUpdateOfRevokedLineBeforeVersioning() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.lineBuilder().id(1L).version(0).status(Status.REVOKED).build();
    LineVersionV3 edited = LineVersionV3TestData.lineBuilder().description("changed").version(0).build();
    doThrow(new RevokedException(current.getSlnid())).when(lineVersionV3ValidationService).validateNotRevoked(current);

    // When / Then
    assertThatThrownBy(() -> lineVersionV3Service.update(current, edited, List.of(current)))
        .isInstanceOf(RevokedException.class);
    verify(lineVersionV3Repository, never()).incrementVersion(anyString());
    verify(versionableService, never()).applyVersioning(any(), anyList(), any(), any());
  }

  @Test
  void shouldValidateUpdateRulesBeforeVersioning() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.lineBuilder().id(1L).version(0).build();
    LineVersionV3 edited = LineVersionV3TestData.lineBuilder().description("changed").version(0).build();

    // When
    lineVersionV3Service.update(current, edited, List.of(current));

    // Then
    InOrder inOrder = inOrder(lineVersionV3ValidationService, lineVersionV3Repository);
    inOrder.verify(lineVersionV3ValidationService).validateNotRevoked(current);
    inOrder.verify(lineVersionV3ValidationService).validateUpdate(current, edited);
    inOrder.verify(lineVersionV3Repository).incrementVersion(LineVersionV3TestData.PARENT_SLNID);
  }

  @Test
  void shouldShortenMembersBeforeParentWhenOnlyValidityIsShortened() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.lineBuilder().id(1L).version(0).build();
    LineVersionV3 edited = LineVersionV3TestData.lineBuilder().validTo(LocalDate.of(2030, 12, 31)).version(0).build();
    LineVersionV3 memberCurrent = LineVersionV3TestData.sublineBuilder().id(2L).version(5).build();
    LineVersionV3 memberEdited = LineVersionV3TestData.sublineBuilder().validTo(LocalDate.of(2030, 12, 31)).version(5)
        .build();
    when(lineV3ShorteningService.isOnlyValidityChanged(current, edited)).thenReturn(true);
    when(lineV3ShorteningService.isShortening(current, edited)).thenReturn(true);
    when(lineV3ShorteningService.checkAndPrepareToShortMembers(current, edited))
        .thenReturn(List.of(new LineV3MemberShortening(memberCurrent, memberEdited)));

    // When
    lineVersionV3Service.update(current, edited, List.of(current));

    // Then
    InOrder inOrder = inOrder(lineVersionV3Repository, versionableService);
    inOrder.verify(lineVersionV3Repository).incrementVersion(LineVersionV3TestData.CHILD_SLNID);
    inOrder.verify(versionableService).versioningObjectsDeletingNullProperties(eq(memberCurrent), eq(memberEdited), anyList());
    inOrder.verify(lineVersionV3Repository).incrementVersion(LineVersionV3TestData.PARENT_SLNID);
    inOrder.verify(versionableService).versioningObjectsDeletingNullProperties(eq(current), eq(edited), anyList());
  }

  @Test
  void shouldNotTouchMembersWhenUpdateIsNotOnlyShorteningValidity() {
    // Given
    LineVersionV3 current = LineVersionV3TestData.lineBuilder().id(1L).version(0).build();
    LineVersionV3 edited = LineVersionV3TestData.lineBuilder().description("changed")
        .validTo(LocalDate.of(2030, 12, 31)).version(0).build();
    when(lineV3ShorteningService.isOnlyValidityChanged(current, edited)).thenReturn(false);
    when(lineV3ShorteningService.isShortening(current, edited)).thenReturn(true);

    // When
    lineVersionV3Service.update(current, edited, List.of(current));

    // Then
    verify(lineV3ShorteningService, never()).checkAndPrepareToShortMembers(any(), any());
    verify(lineVersionV3Repository).incrementVersion(LineVersionV3TestData.PARENT_SLNID);
    verify(lineVersionV3Repository, never()).incrementVersion(LineVersionV3TestData.CHILD_SLNID);
  }

  @Test
  void shouldReturnLineVersionById() {
    // Given
    LineVersionV3 version = LineVersionV3TestData.lineBuilder().id(7L).build();
    when(lineVersionV3Repository.findById(7L)).thenReturn(Optional.of(version));

    // When
    LineVersionV3 result = lineVersionV3Service.getLineVersionById(7L);

    // Then
    assertThat(result).isSameAs(version);
  }

  @Test
  void shouldThrowIdNotFoundExceptionWhenLineVersionIdIsUnknown() {
    // Given
    when(lineVersionV3Repository.findById(7L)).thenReturn(Optional.empty());

    // When / Then
    assertThatThrownBy(() -> lineVersionV3Service.getLineVersionById(7L)).isInstanceOf(IdNotFoundException.class);
  }

  private static LineVersionV3.LineVersionV3Builder<?, ?> newLine() {
    return LineVersionV3TestData.lineBuilder().slnid(null).status(null);
  }

  private void mockSaveGeneratingSlnid() {
    when(lineVersionV3Repository.saveAndFlush(any(LineVersionV3.class))).thenAnswer(invocation -> {
      LineVersionV3 version = invocation.getArgument(0);
      version.setSlnid(GENERATED_SLNID);
      return version;
    });
  }

  private static LineVersionV3 parent(String number, LocalDate validFrom, LocalDate validTo) {
    return LineVersionV3TestData.lineBuilder().number(number).validFrom(validFrom).validTo(validTo).build();
  }

  private void mockChildWithParents(LineVersionV3 child, List<LineVersionV3> parents) {
    when(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(LineVersionV3TestData.CHILD_SLNID)).thenReturn(List.of(child));
    when(lineVersionV3Repository.findAllBySlnidIn(anySet())).thenReturn(parents);
  }

  private static LineVersionV3SearchRestrictions searchRestrictions(Pageable pageable) {
    LineVersionV3SearchRestrictions searchRestrictions = mock(LineVersionV3SearchRestrictions.class);
    when(searchRestrictions.getSpecification()).thenReturn((root, query, criteriaBuilder) -> null);
    when(searchRestrictions.getPageable()).thenReturn(pageable);
    return searchRestrictions;
  }

}
