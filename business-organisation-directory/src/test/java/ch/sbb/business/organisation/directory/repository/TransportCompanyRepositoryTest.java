package ch.sbb.business.organisation.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.bodi.enumeration.TransportCompanyStatus;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
import ch.sbb.business.organisation.directory.entity.TransportCompany.TransportCompanyBuilder;
import ch.sbb.business.organisation.directory.entity.TransportCompanyRelation;
import ch.sbb.business.organisation.directory.entity.TransportCompanyRelation.TransportCompanyRelationBuilder;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
 class TransportCompanyRepositoryTest {

  private static final long TRANSPORT_COMPANY_ID = 5L;

  @Autowired
  private TransportCompanyRepository transportCompanyRepository;

  @Autowired
  private TransportCompanyRelationRepository transportCompanyRelationRepository;

  @AfterEach
  void tearDown() {
    transportCompanyRelationRepository.deleteAll();
    transportCompanyRepository.deleteAll();
  }

  @Test
  void shouldFindInvalidRelationOnStatusInactive() {
    transportCompanyRepository.saveAndFlush(
        transportCompany().transportCompanyStatus(TransportCompanyStatus.INACTIVE).build());
    transportCompanyRelationRepository.saveAndFlush(relation().build());

    List<TransportCompany> result = transportCompanyRepository.findTransportCompaniesWithInvalidRelations();
    assertThat(result).hasSize(1);
  }

  @Test
  void shouldNotFindInvalidRelationOnStatusOperator() {
    transportCompanyRepository.saveAndFlush(
        transportCompany().transportCompanyStatus(TransportCompanyStatus.OPERATOR).build());
    transportCompanyRelationRepository.saveAndFlush(relation().build());

    List<TransportCompany> result = transportCompanyRepository.findTransportCompaniesWithInvalidRelations();
    assertThat(result).isEmpty();
  }

  @Test
  void shouldNotFindInvalidRelationOnTerminatedRelation() {
    transportCompanyRepository.saveAndFlush(
        transportCompany().transportCompanyStatus(TransportCompanyStatus.LIQUIDATED).build());
    transportCompanyRelationRepository.saveAndFlush(
        relation().validTo(LocalDate.now().minusDays(1)).build());

    List<TransportCompany> result = transportCompanyRepository.findTransportCompaniesWithInvalidRelations();
    assertThat(result).isEmpty();
  }

  @Test
  void shouldFindTransportCompaniesWithGivenSboid() {
    transportCompanyRepository.saveAndFlush(transportCompany().build());
    transportCompanyRelationRepository.saveAndFlush(relation().build());

    List<TransportCompany> result = transportCompanyRepository.findAllWithSboid("beste sboid");
    assertThat(result).hasSize(1);
  }

  @Test
  void shouldNotFindTransportCompaniesWithUnknownSboid() {
    transportCompanyRepository.saveAndFlush(transportCompany().build());
    transportCompanyRelationRepository.saveAndFlush(relation().build());

    List<TransportCompany> result = transportCompanyRepository.findAllWithSboid("beste typo sboid");
    assertThat(result).isEmpty();
  }

  TransportCompanyBuilder<?, ?> transportCompany() {
    return TransportCompany.builder()
        .id(TRANSPORT_COMPANY_ID)
        .description("Beste Company")
        .number("#0001")
        .enterpriseId("enterprisige ID");
  }

  TransportCompanyRelationBuilder<?, ?> relation() {
    return TransportCompanyRelation.builder()
        .transportCompany(transportCompany().build())
        .sboid("beste sboid")
        .validFrom(LocalDate.now().minusYears(1))
        .validTo(LocalDate.now().plusYears(1));
  }
}