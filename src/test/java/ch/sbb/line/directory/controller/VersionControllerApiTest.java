package ch.sbb.line.directory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.line.directory.api.ErrorResponse;
import ch.sbb.line.directory.api.VersionModel;
import ch.sbb.line.directory.entity.Version;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.repository.VersionRepository;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class VersionControllerApiTest extends BaseControllerApiTest {

  @Autowired
  private VersionRepository versionRepository;

  private final Version version = Version.builder().ttfnid("ch:1:ttfnid:100000")
                                         .description("FPFN Description")
                                         .number("10.100")
                                         .status(Status.ACTIVE)
                                         .swissTimetableFieldNumber("b0.100")
                                         .validFrom(LocalDate.of(2020, 1, 1))
                                         .validTo(LocalDate.of(2020, 12, 31))
                                         .businessOrganisation("sbb")
                                         .build();

  @BeforeEach
  void createDefaultVersion() {
    versionRepository.save(version);
  }

  @Test
  void shouldReturnOneTimetableFieldNumber() throws Exception {
    mvc.perform(get("/v1/field-numbers")
           .queryParam("page", "0")
           .queryParam("size", "5")
           .queryParam("sort", "swissTimetableFieldNumber,asc"))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$.totalCount").value(1));
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
    List<VersionModel> response = mapper.readValue(responseBody,
        new TypeReference<>() {
        });

    assertThat(response).size().isEqualTo(1);
    VersionModel versionModel = response.get(0);

    // When first update it is ok
    versionModel.setComment("Neuer Kommentar");
    mvc.perform(createUpdateRequest(versionModel)).andExpect(status().isOk());

    // Then on a second update it has to return error for optimistic lock
    versionModel.setComment("Neuer Kommentar wurde erfasst");
    MvcResult mvcResult = mvc.perform(createUpdateRequest(versionModel))
                             .andExpect(status().isPreconditionFailed())
                             .andReturn();
    ErrorResponse errorResponse = mapper.readValue(
        mvcResult.getResponse().getContentAsString(), ErrorResponse.class);

    assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.PRECONDITION_FAILED.value());
    assertThat(errorResponse.getDetails()).size().isEqualTo(1);
    assertThat(errorResponse.getDetails().get(0).getDisplayInfo().getCode()).isEqualTo(
        "COMMON.NOTIFICATION.OPTIMISTIC_LOCK_ERROR");
  }

  @Test
  void shouldReturnNotFoundErrorResponse() throws Exception {
    // Given
    mvc.perform(get("/v1/field-numbers/" + 123)
           .contentType(contentType))
       .andExpect(status().isNotFound())
       .andExpect(jsonPath("$.status", is(404)))
       .andExpect(jsonPath("$.message", is("Entity not found")))
       .andExpect(jsonPath("$.error", is("Not found")))
       .andExpect(jsonPath("$.details[0].message", is("Object with id 123 not found")))
       .andExpect(jsonPath("$.details[0].field", is("id")))
       .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.ENTITY_NOT_FOUND")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("field")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("id")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("value")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("123")));
  }

  @Test
  void shouldReturnValidationNoChangesErrorResponse() throws Exception {
    // Given
    Version secondVersion = Version.builder().ttfnid("ch:1:ttfnid:100000")
                                   .description("FPFN Description")
                                   .number("10.100")
                                   .status(Status.ACTIVE)
                                   .swissTimetableFieldNumber("b0.100")
                                   .validFrom(LocalDate.of(2021, 1, 1))
                                   .validTo(LocalDate.of(2021, 12, 31))
                                   .businessOrganisation("BLS")
                                   .build();
    versionRepository.save(secondVersion);
    //When
    VersionModel versionModel = VersionModel.builder()
                                            .validFrom(version.getValidFrom())
                                            .validTo(version.getValidTo())
                                            .id(version.getId())
                                            .ttfnid(version.getTtfnid())
                                            .description(version.getDescription())
                                            .number(version.getNumber())
                                            .status(version.getStatus())
                                            .swissTimetableFieldNumber(
                                                version.getSwissTimetableFieldNumber())
                                            .businessOrganisation(version.getBusinessOrganisation())
                                            .build();

    //Then

    mvc.perform(post("/v1/field-numbers/versions/" + versionModel.getId())
           .contentType(MediaType.APPLICATION_JSON)
           .content(mapper.writeValueAsString(versionModel)))
       .andExpect(jsonPath("$.status", is(520)))
       .andExpect(jsonPath("$.message", is("No entities were modified after versioning execution.")))
       .andExpect(jsonPath("$.error", is("No changes after versioning")))
       .andExpect(jsonPath("$.details[0].message", is("No entities were modified after versioning execution.")))
       .andExpect(jsonPath("$.details[0].field", is(nullValue())))
       .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.WARNING.VERSIONING_NO_CHANGES")));
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
       .andExpect(jsonPath("$.details[0].message", is("Object with ttfnId 123 not found")))
       .andExpect(jsonPath("$.details[0].field", is("ttfnId")))
       .andExpect(jsonPath("$.details[0].displayInfo.code", is("ERROR.ENTITY_NOT_FOUND")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].key", is("field")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[0].value", is("ttfnId")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].key", is("value")))
       .andExpect(jsonPath("$.details[0].displayInfo.parameters[1].value", is("123")));
  }

  private MockHttpServletRequestBuilder createUpdateRequest(VersionModel versionModel)
      throws JsonProcessingException {
    return post("/v1/field-numbers/versions/" + versionModel.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(versionModel));
  }

  @AfterEach
  void cleanupDb() {
    versionRepository.deleteAll();
  }
}
