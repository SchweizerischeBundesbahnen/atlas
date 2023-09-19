package ch.sbb.business.organisation.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.bodi.enumeration.TransportCompanyStatus;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.business.organisation.directory.controller.TransportCompanySearchRestrictions;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
import ch.sbb.business.organisation.directory.repository.TransportCompanyRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
 class TransportCompanyServiceSearchTest {

  @Autowired
  private TransportCompanyRepository repository;

  @Autowired
  private TransportCompanyService transportCompanyService;

  @BeforeEach
  void createDefaultVersion() {
    repository.save(TransportCompany.builder()
                                    .id(5L)
                                    .description("Beste Company")
                                    .number("#0001")
                                    .enterpriseId("enterprisige ID")
                                    .transportCompanyStatus(TransportCompanyStatus.OPERATOR)
                                    .build());
  }

  @AfterEach
  void cleanUpDb() {
    repository.deleteAll();
  }

  @Test
  void shouldFindVersionByText() {
    //when
    Page<TransportCompany> result = transportCompanyService.getTransportCompanies(
        TransportCompanySearchRestrictions.builder()
                                          .pageable(Pageable.unpaged())
                                          .searchCriterias(List.of("company"))
                                          .build());

    //then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindVersionByText() {
    //when
    Page<TransportCompany> result = transportCompanyService.getTransportCompanies(
        TransportCompanySearchRestrictions.builder()
                                          .pageable(Pageable.unpaged())
                                          .searchCriterias(List.of("wadde hadde dudde da"))
                                          .build());

    //then
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void shouldFindVersionByStatus() {
    //when
    Page<TransportCompany> result = transportCompanyService.getTransportCompanies(
        TransportCompanySearchRestrictions.builder()
                                          .pageable(Pageable.unpaged())
                                          .statusRestrictions(Collections.singletonList(
                                              TransportCompanyStatus.OPERATOR))
                                          .build());

    //then
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void shouldNotFindVersionByStatus() {
    //when
    Page<TransportCompany> result = transportCompanyService.getTransportCompanies(
        TransportCompanySearchRestrictions.builder()
                                          .pageable(Pageable.unpaged())
                                          .statusRestrictions(Collections.singletonList(
                                              TransportCompanyStatus.LIQUIDATED))
                                          .build());

    //then
    assertThat(result.getContent()).isEmpty();
  }
}