package ch.sbb.line.directory.swiss.number;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class SwissNumberUniqueValidator {

  private final EntityManager entityManager;

  public void validate(SwissNumber swissNumber) {
    boolean hasUniqueBusinessIdOverTime = hasUniqueBusinessIdOverTime(swissNumber);
    if (!hasUniqueBusinessIdOverTime) {
      throw new ConflictExcpetion();
    }
  }

  private boolean hasUniqueBusinessIdOverTime(SwissNumber swissNumber) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<?> query = cb.createQuery(swissNumber.getClass());
    Root<?> root = query.from(swissNumber.getClass());

    query.where(
        cb.equal(root.get(swissNumber.getSwissNumber().getName()), swissNumber.getSwissNumber().getValue()),
        cb.and(
            cb.greaterThanOrEqualTo(root.get("validTo"), swissNumber.getValidFrom()),
            cb.lessThanOrEqualTo(root.get("validFrom"), swissNumber.getValidTo())
        )
    );

    List<?> conflicts = entityManager.createQuery(query).getResultList();
    return conflicts.isEmpty();
  }

  private static final class ConflictExcpetion extends ResponseStatusException {

    public ConflictExcpetion() {
      super(HttpStatus.CONFLICT, "SwissNumber already taken in specified period");
    }

  }

}
