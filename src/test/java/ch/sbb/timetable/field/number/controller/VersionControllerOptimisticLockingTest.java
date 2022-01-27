package ch.sbb.timetable.field.number.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.timetable.field.number.IntegrationTest;
import ch.sbb.timetable.field.number.api.ErrorResponse;
import ch.sbb.timetable.field.number.api.VersionModel;
import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.enumaration.Status;
import ch.sbb.timetable.field.number.repository.VersionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
public class VersionControllerOptimisticLockingTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private VersionController versionController;

  @Autowired
  private VersionRepository versionRepository;

  @Autowired
  private ObjectMapper objectMapper;

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
    mockMvc.perform(get("/v1/field-numbers")
               .queryParam("page", "0")
               .queryParam("size", "5")
               .queryParam("sort", "swissTimetableFieldNumber,asc"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.totalCount").value(1));
  }

  @Test
  void shouldReturnOptimisticLockingErrorResponse() throws Exception {
    // Given
    String responseBody = mockMvc.perform(
                                     get("/v1/field-numbers/versions/" + version.getTtfnid()))
                                 .andExpect(status().isOk())
                                 .andReturn()
                                 .getResponse()
                                 .getContentAsString();
    List<VersionModel> response = objectMapper.readValue(responseBody,
        new TypeReference<>() {
        });

    assertThat(response).size().isEqualTo(1);
    VersionModel versionModel = response.get(0);

    // When first update it is ok
    versionModel.setComment("Neuer Kommentar");
    mockMvc.perform(createUpdateRequest(versionModel)).andExpect(status().isOk());

    // Then on a second update it has to return error for optimistic lock
    versionModel.setComment("Neuer Kommentar wurde erfasst");
    MvcResult mvcResult = mockMvc.perform(createUpdateRequest(versionModel))
                                 .andExpect(status().isPreconditionFailed())
                                 .andReturn();
    ErrorResponse errorResponse = objectMapper.readValue(
        mvcResult.getResponse().getContentAsString(), ErrorResponse.class);

    assertThat(errorResponse.getHttpStatus()).isEqualTo(HttpStatus.PRECONDITION_FAILED.value());
    assertThat(errorResponse.getDetails()).size().isEqualTo(1);
    assertThat(errorResponse.getDetails().get(0).getDisplayInfo().getCode()).isEqualTo(
        "COMMON.NOTIFICATION.OPTIMISTIC_LOCK_ERROR");
  }

  private MockHttpServletRequestBuilder createUpdateRequest(VersionModel versionModel)
      throws JsonProcessingException {
    return put("/v1/field-numbers/versions/" + versionModel.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(versionModel));
  }

  @AfterEach
  void cleanupDb() {
    versionRepository.deleteAll();
  }
}
