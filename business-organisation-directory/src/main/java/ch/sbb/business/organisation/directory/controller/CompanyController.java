package ch.sbb.business.organisation.directory.controller;

import ch.sbb.business.organisation.directory.api.CompanyApiV1;
import ch.sbb.business.organisation.directory.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CompanyController implements CompanyApiV1 {

  private final CompanyService companyService;

  @Override
  public void loadCompaniesFromCrd() {
    companyService.saveCompaniesFromCrd();
  }

}
