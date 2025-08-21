package ch.sbb.business.organisation.directory.module.company.controller;

import ch.sbb.atlas.api.bodi.CompanyModel;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.business.organisation.directory.module.company.api.CompanyApiV1;
import ch.sbb.business.organisation.directory.module.company.entity.Company;
import ch.sbb.business.organisation.directory.module.company.mapper.CompanyMapper;
import ch.sbb.business.organisation.directory.module.company.model.CompanySearchRestrictions;
import ch.sbb.business.organisation.directory.module.company.service.CompanyService;
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
public class CompanyControllerV1 implements CompanyApiV1 {

  private final CompanyService companyService;

  @Override
  public Container<CompanyModel> getCompanies(Pageable pageable, List<String> searchCriteria) {
    Page<Company> companies = companyService.getCompanies(
        CompanySearchRestrictions.builder()
            .searchCriterias(searchCriteria)
            .pageable(pageable)
            .build());
    return Container.<CompanyModel>builder()
        .objects(companies.stream()
            .map(CompanyMapper::fromEntity)
            .collect(Collectors.toList()))
        .totalCount(companies.getTotalElements())
        .build();
  }

  @Override
  public CompanyModel getCompany(String uic) {
    return CompanyMapper.fromEntity(companyService.getCompany(uic));
  }

}