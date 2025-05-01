package ch.sbb.atlas.searching.predicates;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class StringPredicatesTest {

  @Mock
  private CriteriaQuery<?> query;

  @Mock
  private CriteriaBuilder criteriaBuilder;

  @Mock
  private Predicate expectedPredicate;

  @Mock
  Expression<String> path;

  @Mock
  Expression<String> lowerPath;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testEqualIgnoreCase() {
    String searchString = "TestString";

    when(criteriaBuilder.lower(path)).thenReturn(lowerPath);
    when(criteriaBuilder.equal(lowerPath, searchString.toLowerCase())).thenReturn(expectedPredicate);

    Predicate result = StringPredicates.equalIgnoreCase(criteriaBuilder, path, searchString);

    assertThat(expectedPredicate).isEqualTo(result);
  }
}
