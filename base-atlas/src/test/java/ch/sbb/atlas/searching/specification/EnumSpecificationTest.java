package ch.sbb.atlas.searching.specification;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.model.Status;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnumSpecificationTest {

  @Mock
  private Root<Object> root;

  @Mock
  private CriteriaQuery<?> query;

  @Mock
  private CriteriaBuilder criteriaBuilder;

  @Mock
  private Predicate expectedPredicate;

  @Mock
  private Path<Status> path;

  @Test
  void shouldNotRestrictWhenRestrictionsAreEmpty() {
    EnumSpecification<Object, Status> specification = new EnumSpecification<>(Collections.emptyList(), "status", false);
    when(criteriaBuilder.and()).thenReturn(expectedPredicate);

    Predicate result = specification.toPredicate(root, query, criteriaBuilder);

    assertThat(result).isSameAs(expectedPredicate);
  }

  /**
   * A blank request param such as {@code ?statusChoices=} is bound by Spring to a list holding a single null element.
   * Without filtering this would render as {@code status IN (null)}, silently matching no rows at all.
   */
  @Test
  void shouldNotRestrictWhenRestrictionsContainOnlyNull() {
    EnumSpecification<Object, Status> specification = new EnumSpecification<>(
        Collections.singletonList(null), "status", false);
    when(criteriaBuilder.and()).thenReturn(expectedPredicate);

    Predicate result = specification.toPredicate(root, query, criteriaBuilder);

    assertThat(result).isSameAs(expectedPredicate);
  }

  @Test
  void shouldIgnoreNullElementsButKeepRemainingRestrictions() {
    when(root.<Status>get("status")).thenReturn(path);
    when(path.in(List.of(Status.VALIDATED))).thenReturn(expectedPredicate);

    EnumSpecification<Object, Status> specification = new EnumSpecification<>(
        Arrays.asList(null, Status.VALIDATED), "status", false);

    Predicate result = specification.toPredicate(root, query, criteriaBuilder);

    assertThat(result).isSameAs(expectedPredicate);
  }

  @Test
  void shouldThrowWhenRestrictionsAreNull() {
    assertThat(
        org.assertj.core.api.Assertions.catchThrowable(() -> new EnumSpecification<Object, Status>(null, "status", false)))
        .isInstanceOf(NullPointerException.class);
  }
}
