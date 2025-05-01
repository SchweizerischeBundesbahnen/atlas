package ch.sbb.atlas.searching.specification;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.searching.predicates.StringPredicates;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class ExactMatchStringSpecificationTest {

  @Mock
  private Root<Object> root;

  @Mock
  private CriteriaQuery<?> query;

  @Mock
  private CriteriaBuilder criteriaBuilder;

  @Mock
  private Predicate expectedPredicate;

  @Mock
  private Path<String> path;

  private MockedStatic<StringPredicates> stringPredicatesMock;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    stringPredicatesMock = Mockito.mockStatic(StringPredicates.class);
  }

  @Test
  void testToPredicateWithEmptyStrings() {
    ExactMatchStringSpecification<Object> specification = new ExactMatchStringSpecification<>(Optional.empty(), "test"
    );
    when(criteriaBuilder.and()).thenReturn(expectedPredicate);

    Predicate result = specification.toPredicate(root, query, criteriaBuilder);

    assertThat(expectedPredicate).isEqualTo(result);
  }

  @Test
  void testToPredicateWithNonEmptyString() {
    when(root.<String>get("test")).thenReturn(path);

    stringPredicatesMock
        .when(() -> StringPredicates.equalIgnoreCase(criteriaBuilder, path, "test"))
        .thenReturn(expectedPredicate);

    ExactMatchStringSpecification<Object> spec =
        new ExactMatchStringSpecification<>(Optional.of("test"), "test");
    Predicate result = spec.toPredicate(root, query, criteriaBuilder);

    assertThat(result).isSameAs(expectedPredicate);
  }
}
