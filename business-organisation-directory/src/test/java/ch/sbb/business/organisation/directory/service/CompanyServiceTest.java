package ch.sbb.business.organisation.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.business.organisation.directory.repository.CompanyRepository;
import ch.sbb.business.organisation.directory.service.crd.Company;
import ch.sbb.business.organisation.directory.service.crd.CompanyDataResponse;
import ch.sbb.business.organisation.directory.service.crd.CrdClientImpl;
import ch.sbb.business.organisation.directory.service.crd.CrdSoapClientConfig;
import java.io.IOException;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.xml.transform.ResourceSource;

class CompanyServiceTest {

  @Mock
  private CompanyRepository repository;

  @Mock
  private CrdClientImpl crdClient;

  private CompanyService companyService;

  @Captor
  private ArgumentCaptor<List<ch.sbb.business.organisation.directory.entity.Company>> companies;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    companyService = new CompanyService(crdClient, repository);
  }

  @Test
  void shouldParseCsvFileFromBavCorrectly() throws IOException {
    // Given
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setContextPath(CrdSoapClientConfig.MARSHALL_CONTEXT_PATH);

    List<Company> result = ((CompanyDataResponse) ((JAXBElement<?>) marshaller.unmarshal(
        new ResourceSource(
            new ClassPathResource("companies.xml")))).getValue()).getCompanyReplicationResponse()
                                                                 .getCompany();

    when(crdClient.getAllCompanies()).thenReturn(result);

    // When
    companyService.saveCompaniesFromCrd();

    // Then
    verify(repository).saveAll(companies.capture());
    assertThat(companies.getValue()).hasSize(1682);
  }

}