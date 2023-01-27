package ch.sbb.business.organisation.directory.controller;

import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
import ch.sbb.business.organisation.directory.entity.TransportCompany.Fields;
import ch.sbb.business.organisation.directory.entity.TransportCompany_;
import ch.sbb.atlas.api.bodi.enumeration.TransportCompanyStatus;
import java.util.List;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
public class TransportCompanySearchRestrictions {

  private final Pageable pageable;

  @Singular(ignoreNullCollections = true)
  private List<String> searchCriterias;

  @Singular(ignoreNullCollections = true)
  private List<TransportCompanyStatus> statusRestrictions;

  public Specification<TransportCompany> getSpecification() {
    return specificationBuilder().searchCriteriaSpecification(searchCriterias)
                                 .and(specificationBuilder().enumSpecification(statusRestrictions,
                                     TransportCompany_.transportCompanyStatus));
  }

  protected SpecificationBuilder<TransportCompany> specificationBuilder() {
    return SpecificationBuilder.<TransportCompany>builder()
                               .stringAttributes(
                                   List.of(
                                       Fields.number,
                                       Fields.abbreviation,
                                       Fields.businessRegisterName,
                                       Fields.description,
                                       Fields.enterpriseId))
                               .build();
  }

}
