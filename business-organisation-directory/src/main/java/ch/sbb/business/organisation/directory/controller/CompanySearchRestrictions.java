package ch.sbb.business.organisation.directory.controller;

import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.business.organisation.directory.entity.Company;
import ch.sbb.business.organisation.directory.entity.Company.Fields;
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
public class CompanySearchRestrictions {

  private final Pageable pageable;

  @Singular(ignoreNullCollections = true)
  private List<String> searchCriterias;

  public Specification<Company> getSpecification() {
    return specificationBuilder().searchCriteriaSpecification(searchCriterias);
  }

  protected SpecificationBuilder<Company> specificationBuilder() {
    return SpecificationBuilder.<Company>builder()
                               .stringAttributes(
                                   List.of(
                                       Fields.uicCode,
                                       Fields.shortName,
                                       Fields.name,
                                       Fields.countryCodeIso,
                                       Fields.url))
                               .build();
  }

}
