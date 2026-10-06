package ch.sbb.line.directory.module.line.search;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.lidi.enumaration.v3.LineObjectType;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LineObjectTypeSpecificationTest {

  @Mock
  private Root<LineVersionV3> root;

  @Mock
  private CriteriaQuery<?> query;

  @Mock
  private CriteriaBuilder criteriaBuilder;

  @Mock
  private Path<Object> referencedLineSlnidPath;

  private AutoCloseable mocks;

  @BeforeEach
  void setUp() {
    mocks = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    mocks.close();
  }

  @Test
  void shouldNotFilterWhenRestrictionsAreNull() {
    //when
    new LineObjectTypeSpecification(null).toPredicate(root, query, criteriaBuilder);

    //then
    verify(criteriaBuilder).and();
    verify(criteriaBuilder, never()).isNull(any());
    verify(criteriaBuilder, never()).isNotNull(any());
  }

  @Test
  void shouldNotFilterWhenRestrictionsAreEmpty() {
    //when
    new LineObjectTypeSpecification(List.of()).toPredicate(root, query, criteriaBuilder);

    //then
    verify(criteriaBuilder).and();
  }

  @Test
  void shouldNotFilterWhenBothTypesAreRequested() {
    //when
    new LineObjectTypeSpecification(List.of(LineObjectType.LINE, LineObjectType.SUBLINE))
        .toPredicate(root, query, criteriaBuilder);

    //then
    verify(criteriaBuilder).and();
    verify(criteriaBuilder, never()).isNull(any());
    verify(criteriaBuilder, never()).isNotNull(any());
  }

  @Test
  void shouldFilterOnNullReferenceForLine() {
    //given
    when(root.get(LineVersionV3.Fields.referencedLineSlnid)).thenReturn(referencedLineSlnidPath);

    //when
    new LineObjectTypeSpecification(List.of(LineObjectType.LINE)).toPredicate(root, query, criteriaBuilder);

    //then
    verify(criteriaBuilder).isNull(referencedLineSlnidPath);
    verify(criteriaBuilder, never()).isNotNull(any());
  }

  @Test
  void shouldFilterOnNonNullReferenceForSubline() {
    //given
    when(root.get(LineVersionV3.Fields.referencedLineSlnid)).thenReturn(referencedLineSlnidPath);

    //when
    new LineObjectTypeSpecification(List.of(LineObjectType.SUBLINE)).toPredicate(root, query, criteriaBuilder);

    //then
    verify(criteriaBuilder).isNotNull(referencedLineSlnidPath);
    verify(criteriaBuilder, never()).isNull(any());
  }

  @Test
  void shouldIgnoreNullAndDuplicateEntries() {
    //given
    when(root.get(LineVersionV3.Fields.referencedLineSlnid)).thenReturn(referencedLineSlnidPath);

    //when
    new LineObjectTypeSpecification(Arrays.asList(LineObjectType.LINE, null, LineObjectType.LINE))
        .toPredicate(root, query, criteriaBuilder);

    //then
    verify(criteriaBuilder).isNull(referencedLineSlnidPath);
  }

}
