package ch.sbb.business.organisation.directory.repository;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.business.organisation.directory.BusinessOrganisationData;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationExportVersionWithTuInfo;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
import ch.sbb.business.organisation.directory.entity.TransportCompanyRelation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
 class BusinessOrganisationVersionExportRepositoryTest {

  private final BusinessOrganisationVersionExportRepository exportRepository;
  private final BusinessOrganisationVersionRepository businessOrganisationVersionRepository;
  private final TransportCompanyRepository transportCompanyRepository;
  private final TransportCompanyRelationRepository transportCompanyRelationRepository;

  @Autowired
   BusinessOrganisationVersionExportRepositoryTest(BusinessOrganisationVersionExportRepository exportRepository,
      BusinessOrganisationVersionRepository businessOrganisationVersionRepository,
      TransportCompanyRepository transportCompanyRepository,
      TransportCompanyRelationRepository transportCompanyRelationRepository) {
    this.exportRepository = exportRepository;
    this.businessOrganisationVersionRepository = businessOrganisationVersionRepository;
    this.transportCompanyRepository = transportCompanyRepository;
    this.transportCompanyRelationRepository = transportCompanyRelationRepository;
  }

  @BeforeEach
  void setUp() {
    businessOrganisationVersionRepository.save(BusinessOrganisationData.businessOrganisationVersionBuilder()
        .validFrom(LocalDate.now().minusYears(1))
        .validTo(LocalDate.now().plusYears(1))
        .build());

    TransportCompany transportCompany = TransportCompany.builder()
        .id(1L)
        .abbreviation("tc")
        .description("Beste Company")
        .number("#0001")
        .enterpriseId("enterprisige ID")
        .businessRegisterName("Next gen sbb")
        .build();
    transportCompanyRepository.saveAndFlush(transportCompany);
    transportCompanyRelationRepository.saveAndFlush(TransportCompanyRelation.builder()
        .sboid("ch:1:sboid:1000000")
        .transportCompany(transportCompany)
        .validFrom(LocalDate.now().minusDays(1))
        .validTo(LocalDate.now().plusDays(1))
        .build());
  }

  @AfterEach
  void tearDown() {
    transportCompanyRelationRepository.deleteAll();
    transportCompanyRepository.deleteAll();
    businessOrganisationVersionRepository.deleteAll();
  }

  @Test
  void shouldExportBusinessOrganisationWithTuInfo() {
    List<BusinessOrganisationExportVersionWithTuInfo> allVersions = exportRepository.findAll();

    assertThat(allVersions).hasSize(1);
    BusinessOrganisationExportVersionWithTuInfo version = allVersions.get(0);
    assertThat(version.getSboid()).isEqualTo("ch:1:sboid:1000000");
    assertThat(version.getBusinessTypes()).hasSize(3);

    assertThat(version.getDescriptionDe()).isEqualTo("desc-de");
    assertThat(version.getNumber()).isEqualTo("#0001");
    assertThat(version.getAbbreviation()).isEqualTo("tc");
    assertThat(version.getBusinessRegisterName()).isEqualTo("Next gen sbb");
  }

  @Test
  void shouldExportBusinessOrganisationWithTuInfoWhenInRelationRange() {
    List<BusinessOrganisationExportVersionWithTuInfo> actualVersions = exportRepository.findVersionsValidOn(LocalDate.now());

    assertThat(actualVersions).hasSize(1);
    BusinessOrganisationExportVersionWithTuInfo version = actualVersions.get(0);
    assertThat(version.getSboid()).isEqualTo("ch:1:sboid:1000000");
    assertThat(version.getDescriptionDe()).isEqualTo("desc-de");

    assertThat(version.getNumber()).isEqualTo("#0001");
    assertThat(version.getAbbreviation()).isEqualTo("tc");
    assertThat(version.getBusinessRegisterName()).isEqualTo("Next gen sbb");
  }

  @Test
  void shouldExportBusinessOrganisationWithoutTuInfoWhenInRelationNotInRange() {
    List<BusinessOrganisationExportVersionWithTuInfo> actualVersions =
        exportRepository.findVersionsValidOn(LocalDate.now().plusDays(5));

    assertThat(actualVersions).hasSize(1);
    BusinessOrganisationExportVersionWithTuInfo version = actualVersions.get(0);
    assertThat(version.getSboid()).isEqualTo("ch:1:sboid:1000000");

    assertThat(version.getNumber()).isNull();
    assertThat(version.getAbbreviation()).isNull();
    assertThat(version.getBusinessRegisterName()).isNull();
  }
}