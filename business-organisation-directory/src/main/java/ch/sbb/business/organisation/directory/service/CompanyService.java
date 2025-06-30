package ch.sbb.business.organisation.directory.service;

import ch.sbb.atlas.model.exception.CountryCodeNotFoundException;
import ch.sbb.business.organisation.directory.controller.CompanySearchRestrictions;
import ch.sbb.business.organisation.directory.entity.Company;
import ch.sbb.business.organisation.directory.repository.CompanyRepository;
import ch.sbb.business.organisation.directory.service.crd.CrdClient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class CompanyService {

  private final CrdClient crdClient;
  private final CompanyRepository companyRepository;

  public Page<Company> getCompanies(
      CompanySearchRestrictions searchRestrictions) {
    return companyRepository.findAll(searchRestrictions.getSpecification(),
        searchRestrictions.getPageable());
  }

  public Company getCompany(String uic) {
    return companyRepository.findById(uic).orElseThrow(() -> new CountryCodeNotFoundException(uic));
  }

  public void saveCompaniesFromCrd() {
    List<Company> companiesFromCrd = getCompaniesFromCrd();
    companyRepository.saveAll(companiesFromCrd);
  }

  List<Company> getCompaniesFromCrd() {
    return crdClient.getAllCompanies().stream().map(this::toEntity).collect(Collectors.toList());
  }

  Company toEntity(ch.sbb.business.organisation.directory.service.crd.Company csvCompany) {
    return Company.builder()
        .uicCode(csvCompany.getCompanyUICCode())
        .name(csvCompany.getCompanyName())
        .url(csvCompany.getCompanyURL().getValue())
        .startValidity(toLocalDate(csvCompany.getStartValidity()))
        .endValidity(toLocalDate(csvCompany.getEndValidity().getValue()))
        .shortName(csvCompany.getCompanyShortName())
        .freeText(csvCompany.getFreeText().getValue())
        .countryCodeIso(csvCompany.getCountry().getCountryCodeISO().getValue())
        .build();
  }

  private static LocalDateTime toLocalDateTime(XMLGregorianCalendar xmlGregorianCalendar) {
    if (xmlGregorianCalendar == null) {
      return null;
    }
    return xmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
  }

  private static LocalDate toLocalDate(XMLGregorianCalendar xmlGregorianCalendar) {
    if (xmlGregorianCalendar == null) {
      return null;
    }
    return toLocalDateTime(xmlGregorianCalendar).toLocalDate();
  }
}
