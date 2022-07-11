package ch.sbb.business.organisation.directory.controller;

import ch.sbb.atlas.model.api.Container;
import ch.sbb.business.organisation.directory.api.CompanyApiV1;
import ch.sbb.business.organisation.directory.api.CompanyModel;
import ch.sbb.business.organisation.directory.entity.Company;
import ch.sbb.business.organisation.directory.service.CompanyService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CompanyController implements CompanyApiV1 {

  private final CompanyService companyService;

  @Override
  public Container<CompanyModel> getCompanies(Pageable pageable, List<String> searchCriteria) {
    Page<Company> companies = companyService.getCompanies(CompanySearchRestrictions.builder()
                                                                                   .searchCriterias(
                                                                                       searchCriteria)
                                                                                   .pageable(
                                                                                       pageable)
                                                                                   .build());
    return Container.<CompanyModel>builder()
                    .objects(companies.stream()
                                      .map(CompanyModel::fromEntity)
                                      .collect(Collectors.toList()))
                    .totalCount(companies.getTotalElements())
                    .build();
  }

  @Override
  public CompanyModel getCompany(Long uic) {
    return CompanyModel.fromEntity(companyService.getCompany(uic));
  }

  @Override
  public void loadCompaniesFromCrd() {
    companyService.saveCompaniesFromCrd();
  }

}
