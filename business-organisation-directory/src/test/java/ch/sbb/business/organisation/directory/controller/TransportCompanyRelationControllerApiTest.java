package ch.sbb.business.organisation.directory.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.business.organisation.directory.api.TransportCompanyRelationModel;
import ch.sbb.business.organisation.directory.api.TransportCompanyRelationModel.Fields;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.entity.BusinessType;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationVersionRepository;
import ch.sbb.business.organisation.directory.repository.TransportCompanyRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TransportCompanyRelationControllerApiTest extends BaseControllerApiTest {

  @Autowired
  private BusinessOrganisationVersionRepository businessOrganisationVersionRepository;
  @Autowired
  private TransportCompanyRepository transportCompanyRepository;

  @Test
  void shouldCreateTransportCompanyRelation() throws Exception {
    businessOrganisationVersionRepository.save(BusinessOrganisationVersion.builder()
                                                                          .sboid(
                                                                              "ch:1:sboid:100500")
                                                                          .abbreviationDe("de")
                                                                          .abbreviationFr("fr")
                                                                          .abbreviationIt("it")
                                                                          .abbreviationEn("en")
                                                                          .descriptionDe("desc-de")
                                                                          .descriptionFr("desc-fr")
                                                                          .descriptionIt("desc-it")
                                                                          .descriptionEn("desc-en")
                                                                          .businessTypes(
                                                                              new HashSet<>(
                                                                                  Arrays.asList(
                                                                                      BusinessType.RAILROAD,
                                                                                      BusinessType.AIR,
                                                                                      BusinessType.SHIP)))
                                                                          .contactEnterpriseEmail(
                                                                              "mail@mail.ch")
                                                                          .organisationNumber(123)
                                                                          .status(Status.ACTIVE)
                                                                          .validFrom(
                                                                              LocalDate.of(2000, 1,
                                                                                  1))
                                                                          .validTo(
                                                                              LocalDate.of(2000, 12,
                                                                                  31))
                                                                          .build());
    transportCompanyRepository.save(TransportCompany.builder()
                                                    .id(5L).build());

    TransportCompanyRelationModel model = TransportCompanyRelationModel.builder()
                                                                       .transportCompanyId(5L)
                                                                       .sboid("ch:1:sboid:100500")
                                                                       .validFrom(
                                                                           LocalDate.of(2020, 5, 5))
                                                                       .validTo(
                                                                           LocalDate.of(2021, 5, 5))
                                                                       .build();

    mvc.perform(post("/v1/transport-company-relations/add").contentType(contentType)
                                                          .content(
                                                              mapper.writeValueAsString(model)))
       .andExpect(status().isCreated())
       .andExpect(jsonPath("$." + Fields.transportCompanyId, is(5)))
       .andExpect(jsonPath("$." + Fields.sboid, is("ch:1:sboid:100500")))
       .andExpect(jsonPath("$." + Fields.validFrom, is("2020-05-05")))
       .andExpect(jsonPath("$." + Fields.validTo, is("2021-05-05")));
  }

}
