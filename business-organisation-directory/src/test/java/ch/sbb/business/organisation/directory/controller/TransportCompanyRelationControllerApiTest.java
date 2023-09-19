package ch.sbb.business.organisation.directory.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.bodi.BusinessOrganisationModel;
import ch.sbb.atlas.api.bodi.TransportCompanyBoRelationModel.Fields;
import ch.sbb.atlas.api.bodi.TransportCompanyRelationModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.business.organisation.directory.BusinessOrganisationData;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
import ch.sbb.business.organisation.directory.entity.TransportCompanyRelation;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationVersionRepository;
import ch.sbb.business.organisation.directory.repository.TransportCompanyRelationRepository;
import ch.sbb.business.organisation.directory.repository.TransportCompanyRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

 class TransportCompanyRelationControllerApiTest extends BaseControllerApiTest {

  private static final TransportCompanyRelationModel RELATION = TransportCompanyRelationModel.builder()
      .transportCompanyId(5L)
      .sboid("ch:1:sboid:1000000")
      .validFrom(LocalDate.of(2022,1,1))
      .validTo(LocalDate.of(2035,1,1))
      .build();

  @Autowired
  private BusinessOrganisationVersionRepository businessOrganisationVersionRepository;
  @Autowired
  private TransportCompanyRepository transportCompanyRepository;

  @Autowired
  private TransportCompanyRelationRepository transportCompanyRelationRepository;

  @BeforeEach
  void setUp() {
    businessOrganisationVersionRepository.save(
        BusinessOrganisationData.businessOrganisationVersion());
    transportCompanyRepository.save(TransportCompany.builder()
                                                    .id(5L).number("5").build());

  }

  @AfterEach
  void cleanUp() {
    transportCompanyRelationRepository.deleteAll();
    businessOrganisationVersionRepository.deleteAll();
    transportCompanyRepository.deleteAll();
  }

  @Test
  void shouldCreateTransportCompanyRelation() throws Exception {
    TransportCompanyRelationModel model = TransportCompanyRelationModel.builder()
                                                                       .transportCompanyId(5L)
                                                                       .sboid("ch:1:sboid:1000000")
                                                                       .validFrom(
                                                                           LocalDate.of(2020, 5, 5))
                                                                       .validTo(
                                                                           LocalDate.of(2021, 5, 5))
                                                                       .build();

    mvc.perform(post("/v1/transport-company-relations").contentType(contentType)
                                                       .content(
                                                           mapper.writeValueAsString(model)))
       .andExpect(status().isCreated())
       .andExpect(jsonPath("$." + Fields.id, notNullValue(Long.class)))
       .andExpect(
           jsonPath(
               "$." + Fields.businessOrganisation + "." + BusinessOrganisationModel.Fields.sboid,
               is("ch:1:sboid:1000000")))
       .andExpect(jsonPath("$." + Fields.validFrom, is("2020-05-05")))
       .andExpect(jsonPath("$." + Fields.validTo, is("2021-05-05")));
  }

  @Test
  void shouldDeleteTransportCompanyRelation() throws Exception {
    TransportCompanyRelation savedRelationEntity = transportCompanyRelationRepository.save(
        TransportCompanyRelation.builder()
                                .sboid("ch:1:sboid:1000000")
                                .transportCompany(
                                    TransportCompany.builder().id(5L).build())
                                .validFrom(LocalDate.of(2020, 1, 1))
                                .validTo(LocalDate.of(2021, 1, 1))
                                .build());

    mvc.perform(delete("/v1/transport-company-relations/" + savedRelationEntity.getId()))
       .andExpect(status().isNoContent());
  }

  @Test
  void shouldGetTransportCompanyRelationsForSpecificTU() throws Exception {
    transportCompanyRepository.save(TransportCompany.builder()
                                                    .id(6L).build());

    TransportCompanyRelation savedRelationOne = transportCompanyRelationRepository.save(
        TransportCompanyRelation.builder()
                                .sboid("ch:1:sboid:1000000")
                                .transportCompany(
                                    TransportCompany.builder().id(5L).build())
                                .validFrom(LocalDate.of(2020, 1, 1))
                                .validTo(LocalDate.of(2021, 1, 1))
                                .build());
    transportCompanyRelationRepository.save(
        TransportCompanyRelation.builder()
                                .sboid("ch:1:sboid:1000000")
                                .transportCompany(
                                    TransportCompany.builder().id(6L).build())
                                .validFrom(LocalDate.of(2023, 1, 1))
                                .validTo(LocalDate.of(2024, 1, 1))
                                .build());

    mvc.perform(get("/v1/transport-company-relations/5"))
       .andExpect(status().isOk())
       .andExpect(jsonPath(
           "$[0]." + Fields.businessOrganisation + "." + BusinessOrganisationModel.Fields.said,
           is("1000000")))
       .andExpect(jsonPath("$[0]." + Fields.businessOrganisation + "."
           + BusinessOrganisationModel.Fields.organisationNumber, is(123)))
       .andExpect(jsonPath("$[0]." + Fields.validFrom, is("2020-01-01")))
       .andExpect(jsonPath("$[0]." + Fields.validTo, is("2021-01-01")))
       .andExpect(jsonPath("$[0]." + Fields.id, is(savedRelationOne.getId().intValue())));
  }

  @Test
  void shouldReportConflictingTransportCompanyRelation() throws Exception {
    TransportCompanyRelationModel model = TransportCompanyRelationModel.builder()
                                                                       .transportCompanyId(5L)
                                                                       .sboid("ch:1:sboid:1000000")
                                                                       .validFrom(
                                                                           LocalDate.of(2020, 5, 5))
                                                                       .validTo(
                                                                           LocalDate.of(2021, 5, 5))
                                                                       .build();
    // First creation of relation is ok
    mvc.perform(post("/v1/transport-company-relations").contentType(contentType)
                                                       .content(mapper.writeValueAsString(model)))
       .andExpect(status().isCreated());

    // Second creation of identical relation should fail
    mvc.perform(post("/v1/transport-company-relations").contentType(contentType)
                                                       .content(mapper.writeValueAsString(model)))
       .andExpect(status().isConflict());
  }

  @Test
  void shouldUpdateTransportCompanyRelation() throws Exception{

    TransportCompanyRelation savedRelationEntity = transportCompanyRelationRepository.save(
        TransportCompanyRelation.builder()
            .sboid("ch:1:sboid:1000000")
            .transportCompany(
                TransportCompany.builder().id(5L).build())
            .validFrom(LocalDate.of(2020, 1, 1))
            .validTo(LocalDate.of(2021, 1, 1))
            .build());

    savedRelationEntity.setValidFrom(LocalDate.of(2030,1,1));
    savedRelationEntity.setValidTo(LocalDate.of(2035,1,1));


    mvc.perform(put("/v1/transport-company-relations").contentType(contentType)
            .content(
                mapper.writeValueAsString(savedRelationEntity)))
        .andExpect(status().isOk());
  }

  @Test
  void shouldThrowConflictWhenUpdatingTransportCompanyRelation() throws Exception{
    TransportCompanyRelation savedRelationEntity = transportCompanyRelationRepository.save(
        TransportCompanyRelation.builder()
            .sboid("ch:1:sboid:1000000")
            .transportCompany(
                TransportCompany.builder().id(5L).build())
            .validFrom(LocalDate.of(2020, 1, 1))
            .validTo(LocalDate.of(2021, 1, 1))
            .build());

    transportCompanyRelationRepository.save(
        TransportCompanyRelation.builder()
            .sboid("ch:1:sboid:1000000")
            .transportCompany(
                TransportCompany.builder().id(5L).build())
            .validFrom(LocalDate.of(2035, 1, 1))
            .validTo(LocalDate.of(2040, 1, 1))
            .build());

    savedRelationEntity.setValidFrom(LocalDate.of(2031,1,1));
    savedRelationEntity.setValidTo(LocalDate.of(2039,1,1));

    mvc.perform(put("/v1/transport-company-relations").contentType(contentType)
            .content(
                mapper.writeValueAsString(savedRelationEntity)))
        .andExpect(status().isConflict());
  }
}
