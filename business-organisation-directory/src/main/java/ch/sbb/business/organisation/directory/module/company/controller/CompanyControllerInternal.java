package ch.sbb.business.organisation.directory.module.company.controller;

import ch.sbb.atlas.api.bodi.CompanyApiInternal;
import ch.sbb.business.organisation.directory.module.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CompanyControllerInternal implements CompanyApiInternal {

  private final CompanyService companyService;

  @Override
  public void loadCompaniesFromCrd() {
    companyService.saveCompaniesFromCrd();
  }

}
