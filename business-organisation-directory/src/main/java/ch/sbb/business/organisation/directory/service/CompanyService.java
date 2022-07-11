package ch.sbb.business.organisation.directory.service;

import ch.sbb.business.organisation.directory.entity.Company;
import ch.sbb.business.organisation.directory.repository.CompanyRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class CompanyService {

  private final CrdClient crdClient;
  private final CompanyRepository companyRepository;

  public void saveCompaniesFromCrd() {
    List<Company> companiesFromCrd = getCompaniesFromCrd();
    companyRepository.saveAll(companiesFromCrd);
  }

  List<Company> getCompaniesFromCrd() {
    return crdClient.getAllCompanies().stream().map(this::toEntity).collect(Collectors.toList());
  }

  Company toEntity(ch.sbb.business.organisation.directory.service.crd.Company csvCompany) {
    return Company.builder()
                  .uicCode(Long.parseLong(csvCompany.getCompanyUICCode()))
                  .name(csvCompany.getCompanyName())
                  .nameAscii(csvCompany.getCompanyNameASCII().getValue())
                  .url(csvCompany.getCompanyURL().getValue())
                  .startValidity(toLocalDateTime(csvCompany.getStartValidity()))
                  .endValidity(toLocalDateTime(csvCompany.getEndValidity().getValue()))
                  .shortName(csvCompany.getCompanyShortName())
                  .freeText(csvCompany.getFreeText().getValue())
                  .countryCodeIso(csvCompany.getCountry().getCountryCodeISO().getValue())
                  .passengerFlag(csvCompany.isPassengerFlag())
                  .freightFlag(csvCompany.isFreightFlag())
                  .infrastructureFlag(csvCompany.isInfrastructureFlag())
                  .otherCompanyFlag(csvCompany.isOtherCompanyFlag())
                  .neEntityFlag(csvCompany.isNEEntityFlag())
                  .ceEntityFlag(csvCompany.isCEEntityFlag())
                  .addDate(toLocalDateTime(csvCompany.getAddDate()))
                  .modifiedDate(toLocalDateTime(csvCompany.getModifiedDate().getValue()))
                  .build();
  }

  private static LocalDateTime toLocalDateTime(XMLGregorianCalendar xmlGregorianCalendar) {
    if (xmlGregorianCalendar == null) {
      return null;
    }
    return xmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
  }
}
