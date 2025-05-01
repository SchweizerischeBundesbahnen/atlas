package ch.sbb.atlas.searching.specification;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testToPredicateWithExactMatchingStrings() {
    ExactMatchStringSpecification<Object> specification = new ExactMatchStringSpecification<>(Optional.empty(), "test"
    );
    when(criteriaBuilder.and()).thenReturn(expectedPredicate);

    Predicate result = specification.toPredicate(root, query, criteriaBuilder);

    assertThat(expectedPredicate).isEqualTo(result);
  }
}
