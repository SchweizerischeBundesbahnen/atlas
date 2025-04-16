package ch.sbb.line.directory.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.api.model.BaseVersionModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.model.search.TimetableFieldNumberSearchRestrictions;
import ch.sbb.line.directory.repository.TimetableFieldNumberVersionRepository;
import ch.sbb.line.directory.service.TimetableFieldNumberService;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.util.TypeInformation;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@MockitoBean(types = AmazonService.class)
class TimetableFieldNumberControllerInternalApiTest extends BaseControllerApiTest {

  @MockitoSpyBean
  private TimetableFieldNumberService timetableFieldNumberService;

  private final TimetableFieldNumberVersionRepository versionRepository;

  @Autowired
  TimetableFieldNumberControllerInternalApiTest(
      TimetableFieldNumberVersionRepository versionRepository) {
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
  void shouldRevokeTimetableFieldNumber() throws Exception {
    mvc.perform(post("/internal/field-numbers/" + version.getTtfnid() + "/revoke"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0]." + BaseVersionModel.Fields.status, is("REVOKED")));
  }

  @Test
  void shouldReturnOneTimetableFieldNumber() throws Exception {
    mvc.perform(get("/internal/field-numbers")
            .queryParam("page", "0")
            .queryParam("size", "5")
            .queryParam("sort", "swissTimetableFieldNumber,asc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount").value(1));
  }

  @Test
  void shouldExportFullTimetableFieldNumberVersionsCsv() throws Exception {
    //when
    mvc.perform(post("/internal/field-numbers/export-csv/full"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  void shouldExportActualTimetableFieldNumberVersionsCsv() throws Exception {
    //when
    mvc.perform(post("/internal/field-numbers/export-csv/actual"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  void shouldExportTimeTableYearChangeTimetableFieldNumberVersionsCsv() throws Exception {
    //when
    mvc.perform(post("/internal/field-numbers/export-csv/timetable-year-change"))
        .andExpect(status().isOk()).andReturn();
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
