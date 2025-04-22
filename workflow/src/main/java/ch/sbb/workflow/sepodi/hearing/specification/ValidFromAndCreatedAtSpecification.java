package ch.sbb.workflow.sepodi.hearing.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ValidFromAndCreatedAtSpecification<T> implements Specification<T> {

    @Serial
    private static final long serialVersionUID = 1;
    private final LocalDate versionValidFrom;
    private final LocalDateTime createdAt;


    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (versionValidFrom != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("versionValidFrom"), versionValidFrom));
        }
        if (createdAt != null) {
            predicates.add(criteriaBuilder.equal(root.get("creationDate"), createdAt));
        }
        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
