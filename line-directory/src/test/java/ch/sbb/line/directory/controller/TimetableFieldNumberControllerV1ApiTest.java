package ch.sbb.line.directory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.lidi.TimetableFieldNumberVersionModel;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.repository.TimetableFieldNumberVersionRepository;
import ch.sbb.line.directory.service.TimetableFieldNumberValidationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@MockitoBean(types = TimetableFieldNumberValidationService.class)
class TimetableFieldNumberControllerV1ApiTest extends BaseControllerApiTest {

  private final TimetableFieldNumberVersionRepository versionRepository;

  @Autowired
  TimetableFieldNumberControllerV1ApiTest(TimetableFieldNumberVersionRepository versionRepository) {
    this.versionRepository = versionRepository;
  }

  private TimetableFieldNumberVersion version =
      TimetableFieldNumberVersion.builder()
          .ttfnid("ch:1:ttfnid:100000")
          .description("FPFN Description")
          .number("10.100")
          .status(Status.VALIDATED)
          .swissTimetableFieldNumber("b0.100")
          .validFrom(LocalDate.of(2020, 1, 1))
          .validTo(LocalDate.of(2020, 12, 31))
          .businessOrganisation("sbb")
          .build();

  @BeforeEach
  void createDefaultVersion() {
    version = versionRepository.saveAndFlush(version);
  }

  @AfterEach
  void cleanupDb() {
    versionRepository.deleteAll();
  }

  @Test
  void shouldCreateTimetableFieldNumber() throws Exception {
    //given
    TimetableFieldNumberVersionModel timetableFieldNumberVersionModel =
        TimetableFieldNumberVersionModel.builder()
            .validTo(LocalDate.of(2000, 12, 31))
            .validFrom(LocalDate.of(2000, 1, 1))
            .businessOrganisation("sbb")
            .swissTimetableFieldNumber("swissLineNumber")
            .number("123")
            .description("description")
            .ttfnid("123")
            .status(Status.VALIDATED).build();
    //when && then
    mvc.perform(post("/v1/field-numbers/versions")
        .contentType(contentType)
        .content(mapper.writeValueAsString(timetableFieldNumberVersionModel))
    ).andExpect(status().isCreated());
  }

  @Test
  void shouldReturnOptimisticLockingErrorResponse() throws Exception {
    // Given
    String responseBody = mvc.perform(
            get("/v1/field-numbers/versions/" + version.getTtfnid()))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    List<TimetableFieldNumberVersionModel> response = mapper.readValue(responseBody,
        new TypeReference<>() {
        });

    assertThat(response).size().isEqualTo(1);
    TimetableFieldNumberVersionModel timetableFieldNumberVersionModel = response.get(0);

    // When first update it is ok
    timetableFieldNumberVersionModel.setComment("Neuer Kommentar");
    mvc.perform(createUpdateRequest(timetableFieldNumberVersionModel)).andExpect(status().isOk());

    // Then on a second update it has to return error for optimistic lock
    timetableFieldNumberVersionModel.setComment("Neuer Kommentar wurde erfasst");
    MvcResult mvcResult = mvc.perform(createUpdateRequest(timetableFieldNumberVersionModel))
        .andExpect(status().isPreconditionFailed())
        .andReturn();
    ErrorResponse errorResponse = mapper.readValue(
        mvcResult.getResponse().getContentAsString(), ErrorResponse.class);

    assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.PRECONDITION_FAILED.value());
    assertThat(errorResponse.getDetails()).size().isEqualTo(1);
    assertThat(errorResponse.getDetails().first().getDisplayInfo().getCode()).isEqualTo(
        "COMMON.NOTIFICATION.OPTIMISTIC_LOCK_ERROR");
  }

  @Test
  void shouldReturnValidationNoChangesErrorResponse() throws Exception {
    // Given
    TimetableFieldNumberVersion secondVersion = TimetableFieldNumberVersion.builder()
        .ttfnid("ch:1:ttfnid:100000")
        .description("FPFN Description")
        .number("10.100")
        .status(Status.VALIDATED)
        .swissTimetableFieldNumber("b0.100")
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .businessOrganisation("BLS")
        .build();
    versionRepository.saveAndFlush(secondVersion);
    //When
    TimetableFieldNumberVersionModel timetableFieldNumberVersionModel = TimetableFieldNumberVersionModel.builder()
        .id(version.getId())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .ttfnid(version.getTtfnid())
        .description(version.getDescription())
        .number(version.getNumber())
        .status(version.getStatus())
        .swissTimetableFieldNumber(version.getSwissTimetableFieldNumber())
        .businessOrganisation(version.getBusinessOrganisation())
        .creationDate(version.getCreationDate())
        .editionDate(version.getEditionDate())
        .creator(version.getCreator())
        .editor(version.getEditor())
        .etagVersion(version.getVersion())
        .build();

    //Then
    mvc.perform(post("/v1/field-numbers/versions/" + timetableFieldNumberVersionModel.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(timetableFieldNumberVersionModel)))
        .andExpect(jsonPath("$.status", is(520)))
        .andExpect(
            jsonPath("$.message", is("No entities were modified after versioning execution.")))
        .andExpect(jsonPath("$.error", is("No changes after versioning")))
        .andExpect(jsonPath("$.details[0].message",
            is("No entities were modified after versioning execution.")))
        .andExpect(jsonPath("$.details[0].field", is(nullValue())))
        .andExpect(
            jsonPath("$.details[0].displayInfo.code", is("ERROR.WARNING.VERSIONING_NO_CHANGES")));
  }

  @Test
  void shouldReturnNotFoundErrorResponseWhenSearchItemNotFound() throws Exception {
    // Given
    mvc.perform(get("/v1/field-numbers/versions/" + 123)
            .contentType(contentType))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("Entity not found")))
        .andExpect(jsonPath("$.error", is("Not found")))
        .andExpect(jsonPath("$.details[0].message", is("Object with ttfnid 123 not found")))
        .andExpect(jsonPath("$.details[0].field", is("ttfnid")))
        .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.ENTITY_NOT_FOUND")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("field")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("ttfnid")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("value")))
        .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("123")));
  }

  @Test
  void shouldReturnOptimisticLockingOnBusinessObjectChanges() throws Exception {
    //given

    // When first update it is ok
    version.setValidFrom(LocalDate.of(2025, 1, 1));
    version.setValidTo(LocalDate.of(2025, 12, 31));
    mvc.perform(createUpdateRequest(TimetableFieldNumberControllerV1.toModel(version)))
        .andExpect(status().isOk());

    // Then on a second update it has to return error for optimistic lock
    version.setValidFrom(LocalDate.of(2000, 1, 1));
    version.setValidTo(LocalDate.of(2025, 12, 31));
    mvc.perform(createUpdateRequest(TimetableFieldNumberControllerV1.toModel(version)))
        .andExpect(status().isPreconditionFailed());
  }

  private MockHttpServletRequestBuilder createUpdateRequest(
      TimetableFieldNumberVersionModel timetableFieldNumberVersionModel)
      throws JsonProcessingException {
    return post("/v1/field-numbers/versions/" + timetableFieldNumberVersionModel.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(timetableFieldNumberVersionModel));
  }

}
