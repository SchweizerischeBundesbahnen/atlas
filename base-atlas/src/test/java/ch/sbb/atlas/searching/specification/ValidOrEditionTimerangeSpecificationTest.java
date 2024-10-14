package ch.sbb.atlas.searching.specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ValidOrEditionTimerangeSpecificationTest {

  @Mock
  private Root<Object> root;

  @Mock
  private CriteriaQuery<?> query;

  @Mock
  private CriteriaBuilder criteriaBuilder;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testToPredicateWithDates() {
    LocalDate fromDate = LocalDate.of(2023, 1, 1);
    LocalDate toDate = LocalDate.of(2023, 12, 31);
    LocalDate validToFromDate = LocalDate.of(2023, 6, 1);
    LocalDateTime createdAfter = LocalDateTime.of(2023, 2, 1, 0, 0);
    LocalDateTime modifiedAfter = LocalDateTime.of(2023, 3, 1, 0, 0);

    ValidOrEditionTimerangeSpecification<Object> specification = new ValidOrEditionTimerangeSpecification<>(
        fromDate, toDate, validToFromDate, createdAfter, modifiedAfter
    );

    when(root.get("validFrom")).thenReturn(null);
    when(root.get("validTo")).thenReturn(null);
    when(root.get("creationDate")).thenReturn(null);
    when(root.get("editionDate")).thenReturn(null);

    Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
    assertThat(predicate).isNull();
  }

  @Test
  void testToPredicateWithNullDates() {
    ValidOrEditionTimerangeSpecification<Object> specification = new ValidOrEditionTimerangeSpecification<>(
        null, null, null, null, null
    );

    Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
    assertThat(predicate).isNull();
  }

}
