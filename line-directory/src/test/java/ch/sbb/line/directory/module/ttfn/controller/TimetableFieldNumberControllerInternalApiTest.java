package ch.sbb.line.directory.module.ttfn.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.api.lidi.enumaration.TtfnMeanOfTransport;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldNumber;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.module.ttfn.repository.TimetableFieldNumberVersionRepository;
import ch.sbb.line.directory.module.ttfn.search.TimetableFieldNumberSearchRestrictions;
import ch.sbb.line.directory.module.ttfn.service.TimetableFieldNumberService;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.data.core.TypeInformation;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@MockitoBean(types = AmazonService.class)
class TimetableFieldNumberControllerInternalApiTest extends BaseControllerApiTest {

  @MockitoSpyBean
  private TimetableFieldNumberService timetableFieldNumberService;

  private final TimetableFieldNumberVersionRepository versionRepository;

  @Autowired
  TimetableFieldNumberControllerInternalApiTest(TimetableFieldNumberVersionRepository versionRepository) {
    this.versionRepository = versionRepository;
  }

  private TimetableFieldNumberVersion version =
      TimetableFieldNumberVersion.builder()
          .ttfnid("ch:1:ttfnid:100000")
          .descriptionOutwardLine1("FPFN Outward Line Desc")
          .descriptionReturnLine1("FPFN Return Line Desc")
          .meanOfTransport(TtfnMeanOfTransport.TRAIN)
          .number("10.100")
          .status(Status.VALIDATED)
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
  void shouldRevokeTimetableFieldNumber() throws Exception {
    mvc.perform(post("/internal/field-numbers/" + version.getTtfnid() + "/revoke"))
        .andExpect(status().isOk());
  }

  @Test
  void shouldReturnOneTimetableFieldNumber() throws Exception {
    mvc.perform(get("/internal/field-numbers")
            .queryParam("page", "0")
            .queryParam("size", "5")
            .queryParam("sort", "number,asc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(1));
  }

  @Test
  void shouldReturnExpiredTimetableFieldNumberFilteredByTtfnIds() throws Exception {
    mvc.perform(get("/internal/field-numbers")
            .queryParam("ttfnIds", version.getTtfnid()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(1))
        .andExpect(jsonPath("$.objects[0].ttfnid").value(version.getTtfnid()))
        .andExpect(jsonPath("$.objects[0].number").value(version.getNumber()));
  }

  @Test
  void shouldFilterByTtfnIdsWhenEmptyStatusChoicesParamIsSentByFeignClient() throws Exception {
    mvc.perform(get("/internal/field-numbers")
            .queryParam("searchCriteria", "")
            .queryParam("statusChoices", "")
            .queryParam("ttfnIds", version.getTtfnid()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(1));
  }

  @Test
  void shouldIgnoreEmptySearchCriteriaParam() throws Exception {
    mvc.perform(get("/internal/field-numbers")
            .queryParam("searchCriteria", "")
            .queryParam("ttfnIds", version.getTtfnid()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(1));
  }

  @Test
  void shouldIgnoreEmptyStatusChoicesParam() throws Exception {
    mvc.perform(get("/internal/field-numbers")
            .queryParam("statusChoices", "")
            .queryParam("ttfnIds", version.getTtfnid()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(1));
  }

  @Test
  void shouldReturnEmptyResultForUnknownTtfnIds() throws Exception {
    mvc.perform(get("/internal/field-numbers")
            .queryParam("ttfnIds", "ch:1:ttfnid:does-not-exist"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(0));
  }

  @Test
  void shouldIgnoreTtfnIdsWhenParamOmitted() throws Exception {
    mvc.perform(get("/internal/field-numbers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(1));
  }

  @Test
  void shouldExcludeExpiredTimetableFieldNumberWhenExcludeExpiredIsTrue() throws Exception {
    mvc.perform(get("/internal/field-numbers")
            .queryParam("excludeExpired", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(0));
  }

  @Test
  void shouldKeepExpiredTimetableFieldNumberWhenExcludeExpiredOmitted() throws Exception {
    mvc.perform(get("/internal/field-numbers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(1));
  }

  @Test
  void shouldReturnBadRequestExceptionOnInvalidSortParam() throws Exception {
    // given
    Mockito.doThrow(
            new PropertyReferenceException("nam", TypeInformation.of(TimetableFieldNumber.class), Collections.emptyList()))
        .when(timetableFieldNumberService).getVersionsSearched(Mockito.any(TimetableFieldNumberSearchRestrictions.class));
    // when/then
    mvc.perform(get("/internal/field-numbers")
            .queryParam("page", "0")
            .queryParam("size", "5")
            .queryParam("sort", "nam,asc"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message")
            .value("Supplied sort field nam not found on TimetableFieldNumber"));
  }

}
