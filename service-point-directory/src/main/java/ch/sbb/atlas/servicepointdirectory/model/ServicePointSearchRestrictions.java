package ch.sbb.atlas.servicepointdirectory.model;

import ch.sbb.atlas.servicepointdirectory.api.ServicePointRequestParams;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Data
@Builder
public class ServicePointSearchRestrictions {

  private final Pageable pageable;

  private ServicePointVersionSpecification specification;

  @Data
  public static class ServicePointVersionSpecification implements Specification<ServicePointVersion> {

    private final ServicePointRequestParams servicePointRequestParams;

    @Override
    public Predicate toPredicate(Root<ServicePointVersion> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
      // TODO: implement all the shit
      return null;
    }
  }

}
