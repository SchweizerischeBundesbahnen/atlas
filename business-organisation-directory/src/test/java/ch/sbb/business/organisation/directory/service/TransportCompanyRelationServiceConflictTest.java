package ch.sbb.business.organisation.directory.service;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.business.organisation.directory.BusinessOrganisationData;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
import ch.sbb.business.organisation.directory.entity.TransportCompanyRelation;
import ch.sbb.business.organisation.directory.exception.TransportCompanyRelationConflictException;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationVersionRepository;
import ch.sbb.business.organisation.directory.repository.TransportCompanyRelationRepository;
import ch.sbb.business.organisation.directory.repository.TransportCompanyRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
 class TransportCompanyRelationServiceConflictTest {

  private static final String SBOID_1 = "ch:1:sboid:1";
  private static final String SBOID_2 = "ch:1:sboid:2";

  @Autowired
  private BusinessOrganisationService businessOrganisationService;

  @Autowired
  private BusinessOrganisationVersionRepository businessOrganisationVersionRepository;

  @Autowired
  private TransportCompanyService transportCompanyService;

  @Autowired
  private TransportCompanyRepository transportCompanyRepository;

  @Autowired
  private TransportCompanyRelationService transportCompanyRelationService;

  @Autowired
  private TransportCompanyRelationRepository transportCompanyRelationRepository;

  private TransportCompany transportCompany1;
  private TransportCompany transportCompany2;

  @BeforeEach
  void setUp() {
    businessOrganisationVersionRepository.save(
        BusinessOrganisationData.businessOrganisationVersionBuilder()
                                .sboid(SBOID_1)
                                .build());
    businessOrganisationVersionRepository.save(
        BusinessOrganisationData.businessOrganisationVersionBuilder()
                                .sboid(SBOID_2)
                                .build());

    transportCompany1 = transportCompanyRepository.save(
        TransportCompany.builder().id(1L).number("TU 1").build());
    transportCompany2 = transportCompanyRepository.save(
        TransportCompany.builder().id(2L).number("TU 2").build());
  }

  @AfterEach
  void cleanUp() {
    transportCompanyRelationRepository.deleteAll();
    businessOrganisationVersionRepository.deleteAll();
    transportCompanyRepository.deleteAll();
  }

  @Test
  void shouldSaveTransportCompanyRelationWhenTwoTCsRelateToTwoBOs() {
    TransportCompanyRelation firstRelation = TransportCompanyRelation.builder()
                                                                     .transportCompany(
                                                                         transportCompany1)
                                                                     .sboid(SBOID_1)
                                                                     .validFrom(
                                                                         LocalDate.of(2000, 1, 1))
                                                                     .validTo(
                                                                         LocalDate.of(2000, 12, 31))
                                                                     .build();
    assertDoesNotThrow(() -> transportCompanyRelationService.save(firstRelation, false));

    TransportCompanyRelation secondRelation = TransportCompanyRelation.builder()
                                                                      .transportCompany(
                                                                          transportCompany2)
                                                                      .sboid(SBOID_2)
                                                                      .validFrom(
                                                                          LocalDate.of(2000, 1, 1))
                                                                      .validTo(
                                                                          LocalDate.of(2000, 12,
                                                                              31))
                                                                      .build();
    assertDoesNotThrow(() -> transportCompanyRelationService.save(secondRelation, false));
  }

  @Test
  void shouldSaveTransportCompanyRelationWhenTwoTCsRelateToOneBOwithoutOverlap() {
    TransportCompanyRelation firstRelation = TransportCompanyRelation.builder()
                                                                     .transportCompany(
                                                                         transportCompany1)
                                                                     .sboid(SBOID_1)
                                                                     .validFrom(
                                                                         LocalDate.of(2000, 1, 1))
                                                                     .validTo(
                                                                         LocalDate.of(2000, 12, 31))
                                                                     .build();
    assertDoesNotThrow(() -> transportCompanyRelationService.save(firstRelation, false));

    TransportCompanyRelation secondRelation = TransportCompanyRelation.builder()
                                                                      .transportCompany(
                                                                          transportCompany2)
                                                                      .sboid(SBOID_1)
                                                                      .validFrom(
                                                                          LocalDate.of(2001, 1, 1))
                                                                      .validTo(
                                                                          LocalDate.of(2001, 12,
                                                                              31))
                                                                      .build();
    assertDoesNotThrow(() -> transportCompanyRelationService.save(secondRelation, false));
  }

  @Test
  void shouldNotSaveTransportCompanyRelationWhenTwoTCsRelateToOneBOwithOverlap() {
    TransportCompanyRelation firstRelation = TransportCompanyRelation.builder()
                                                                     .transportCompany(
                                                                         transportCompany1)
                                                                     .sboid(SBOID_1)
                                                                     .validFrom(
                                                                         LocalDate.of(2000, 1, 1))
                                                                     .validTo(
                                                                         LocalDate.of(2000, 12, 31))
                                                                     .build();
    assertDoesNotThrow(() -> transportCompanyRelationService.save(firstRelation, false));

    TransportCompanyRelation secondRelation = TransportCompanyRelation.builder()
                                                                      .transportCompany(
                                                                          transportCompany2)
                                                                      .sboid(SBOID_1)
                                                                      .validFrom(
                                                                          LocalDate.of(2000, 1, 1))
                                                                      .validTo(
                                                                          LocalDate.of(2001, 12,
                                                                              31))
                                                                      .build();
    assertThrows(TransportCompanyRelationConflictException.class,
        () -> transportCompanyRelationService.save(secondRelation, false));
  }

  @Test
  void shouldSaveTransportCompanyRelationWhenOneTCRelatesToTwoBOwithOverlap() {
    TransportCompanyRelation firstRelation = TransportCompanyRelation.builder()
                                                                     .transportCompany(
                                                                         transportCompany1)
                                                                     .sboid(SBOID_1)
                                                                     .validFrom(
                                                                         LocalDate.of(2000, 1, 1))
                                                                     .validTo(
                                                                         LocalDate.of(2000, 12, 31))
                                                                     .build();
    assertDoesNotThrow(() -> transportCompanyRelationService.save(firstRelation, false));

    TransportCompanyRelation secondRelation = TransportCompanyRelation.builder()
                                                                      .transportCompany(
                                                                          transportCompany1)
                                                                      .sboid(SBOID_2)
                                                                      .validFrom(
                                                                          LocalDate.of(2000, 1, 1))
                                                                      .validTo(
                                                                          LocalDate.of(2000, 12,
                                                                              31))
                                                                      .build();
    assertDoesNotThrow(() -> transportCompanyRelationService.save(secondRelation,false));
  }

}
