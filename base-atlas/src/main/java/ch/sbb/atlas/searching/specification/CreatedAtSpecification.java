package ch.sbb.atlas.searching.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
public class CreatedAtSpecification<T> implements Specification<T> {

    @Serial
    private static final long serialVersionUID = 1;

    private final LocalDateTime createdAt;


    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if (createdAt != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("creationDate"), createdAt));
        }
        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
