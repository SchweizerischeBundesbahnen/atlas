package ch.sbb.line.directory.module.line.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.lidi.LineVersionModelV3;
import ch.sbb.atlas.api.lidi.enumaration.v3.ConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineObjectType;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.repository.LineVersionV3Repository;
import ch.sbb.line.directory.module.line.search.LineVersionV3SearchRestrictions;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

class LineVersionV3ServiceTest {

  private static final int SUBLINE_ROW_COUNT = 5;

  @Mock
  private LineVersionV3Repository lineVersionV3Repository;

  private LineVersionV3Service lineVersionV3Service;

  private AutoCloseable mocks;

  @BeforeEach
  void setUp() {
    mocks = MockitoAnnotations.openMocks(this);
    lineVersionV3Service = new LineVersionV3Service(lineVersionV3Repository);
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
