package ch.sbb.line.directory.module.line.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.lidi.CreateLineVersionModelV3;
import ch.sbb.atlas.api.lidi.UpdateLineVersionModelV3;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.api.lidi.enumaration.v3.ConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.v3.Financing;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineMeanOfTransport;
import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.user.administration.security.service.BusinessOrganisationBasedUserAdministrationService;
import ch.sbb.line.directory.module.line.LineVersionV3TestData;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.repository.LineVersionV3Repository;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Covers the SUPERVISOR-only fields end to end. The user administration service is mocked to simulate a writer who holds the
 * write permission on the business organisation but not the SUPERVISOR role.
 */
@MockitoBean(types = SharedBusinessOrganisationService.class)
class LineControllerV3SupervisorFieldApiTest extends BaseControllerApiTest {

  private static final String VERSIONS_URL = "/v3/lines/versions";
  private static final String SBOID = "ch:1:sboid:1100048";

  private final LineVersionV3Repository lineVersionV3Repository;

  @MockitoBean
  private BusinessOrganisationBasedUserAdministrationService userAdministrationService;

  @Autowired
  LineControllerV3SupervisorFieldApiTest(LineVersionV3Repository lineVersionV3Repository) {
    this.lineVersionV3Repository = lineVersionV3Repository;
  }

  @BeforeEach
  void mockWriterWithoutSupervisorRole() {
    when(userAdministrationService.hasUserPermissionsToCreate(any(), any())).thenReturn(true);
    when(userAdministrationService.hasUserPermissionsToUpdate(any(), any(), any())).thenReturn(true);
    when(userAdministrationService.isAtLeastSupervisor(ApplicationType.LIDI)).thenReturn(false);
  }

  @AfterEach
  void tearDown() {
    lineVersionV3Repository.deleteAll();
  }

  @Test
  void shouldNotCreateLineWithSupervisorOnlyFieldsAsWriter() throws Exception {
    // Given
    CreateLineVersionModelV3 model = lineModel().financing(Financing.RPV).cablewayNumber("3521").build();

    // When
    ResultActions result = create(model);

    // Then
    result.andExpect(status().isForbidden())
        .andExpect(jsonPath("$.details[*].field", containsInAnyOrder("financing", "cablewayNumber")))
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("LIDI.LINE.ERROR.SUPERVISOR_ONLY_FIELD")));
    assertThat(lineVersionV3Repository.findAll()).isEmpty();
  }

  @Test
  void shouldCreateLineWithoutSupervisorOnlyFieldsAsWriter() throws Exception {
    // Given
    CreateLineVersionModelV3 model = lineModel().build();

    // When
    ResultActions result = create(model);

    // Then
    result.andExpect(status().isCreated());
    assertThat(lineVersionV3Repository.findAll()).hasSize(1);
  }

  @Test
  void shouldNotUpdateSupervisorOnlyFieldAsWriter() throws Exception {
    // Given
    LineVersionV3 saved = lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());
    UpdateLineVersionModelV3 model = updateModelOf(saved).financing(Financing.RPV).build();

    // When
    ResultActions result = update(saved.getId(), model);

    // Then
    result.andExpect(status().isForbidden())
        .andExpect(jsonPath("$.details[*].field", containsInAnyOrder("financing")));
    assertThat(lineVersionV3Repository.findById(saved.getId()).orElseThrow().getFinancing())
        .isEqualTo(Financing.SELF_FINANCED);
  }

  @Test
  void shouldUpdateOtherFieldsWhileResendingTheUnchangedSupervisorOnlyFieldsAsWriter() throws Exception {
    // Given
    LineVersionV3 saved = lineVersionV3Repository.saveAndFlush(LineVersionV3TestData.line());
    UpdateLineVersionModelV3 model = updateModelOf(saved).description("Updated description").build();

    // When
    ResultActions result = update(saved.getId(), model);

    // Then
    result.andExpect(status().isOk());
    assertThat(lineVersionV3Repository.findById(saved.getId()).orElseThrow().getDescription())
        .isEqualTo("Updated description");
  }

  private CreateLineVersionModelV3.CreateLineVersionModelV3Builder<?, ?> lineModel() {
    return CreateLineVersionModelV3.builder()
        .lineType(LineType.ORDERLY)
        .concessionType(ConcessionType.CANTONALLY_APPROVED_LINE)
        .number("L1")
        .offerCategory(OfferCategory.B)
        .swissLineNumber("90.100")
        .description("Supervisor field test")
        .businessOrganisation(SBOID)
        .meansOfTransport(Set.of(LineMeanOfTransport.TRAIN))
        .validFrom(LocalDate.of(2026, 1, 1))
        .validTo(LocalDate.of(9999, 12, 31));
  }

  private UpdateLineVersionModelV3.UpdateLineVersionModelV3Builder<?, ?> updateModelOf(LineVersionV3 version) {
    return UpdateLineVersionModelV3.builder()
        .etagVersion(version.getVersion())
        .concessionType(version.getConcessionType())
        .number(version.getNumber())
        .swissLineNumber(version.getSwissLineNumber())
        .offerCategory(version.getOfferCategory())
        .longName(version.getLongName())
        .description(version.getDescription())
        .businessOrganisation(version.getBusinessOrganisation())
        .financing(version.getFinancing())
        .meansOfTransport(version.getMeansOfTransport())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo());
  }

  private ResultActions create(CreateLineVersionModelV3 model) throws Exception {
    return mvc.perform(post(VERSIONS_URL).contentType(contentType).content(mapper.writeValueAsString(model)));
  }

  private ResultActions update(Long id, UpdateLineVersionModelV3 model) throws Exception {
    return mvc.perform(put(VERSIONS_URL + "/" + id).contentType(contentType).content(mapper.writeValueAsString(model)));
  }

}
