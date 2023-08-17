package ch.sbb.atlas.servicepointdirectory.service.loadingpoint;

import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion_;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
public class LoadingPointServicePointSpecification <T> implements Specification<T> {

    @Serial
    private static final long serialVersionUID = 1;

    private final List<String> sboids;

    private final List<Integer> shortNumbers;

    private final List<Country> countries;

    private final List<String> sloids;

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Subquery<?> servicePointSubquery = query.subquery(ServicePointVersion.class);
        Root<ServicePointVersion> fromServicePoint = servicePointSubquery.from(ServicePointVersion.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(fromServicePoint.get(ServicePointVersion.Fields.number), root.get(LoadingPointVersion.Fields.servicePointNumber)));
        if (!sboids.isEmpty()) {
            predicates.add(criteriaBuilder.and(fromServicePoint.get(ServicePointVersion_.businessOrganisation).in(sboids)));
        }
        if (!shortNumbers.isEmpty()) {
            predicates.add(criteriaBuilder.and(fromServicePoint.get(ServicePointVersion_.numberShort).in(shortNumbers)));
        }
        if (!countries.isEmpty()) {
            predicates.add(criteriaBuilder.and(fromServicePoint.get(ServicePointVersion_.country).in(countries)));
        }
        if (!sloids.isEmpty()) {
            predicates.add(criteriaBuilder.and(fromServicePoint.get(ServicePointVersion_.sloid).in(sloids)));
        }
        servicePointSubquery.where(predicates.toArray(new Predicate[]{}));
        return criteriaBuilder.exists(servicePointSubquery);
    }
}
