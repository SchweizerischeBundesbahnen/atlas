package ch.sbb.line.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingYearModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingYearModel.Fields;
import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.line.directory.repository.TimetableHearingYearRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

 class TimetableHearingYearControllerApiTest extends BaseControllerApiTest {

  private static final long YEAR = 2022L;
  private static final TimetableHearingYearModel TIMETABLE_HEARING_YEAR = TimetableHearingYearModel.builder()
      .timetableYear(YEAR)
      .hearingFrom(LocalDate.of(2021, 1, 1))
      .hearingTo(LocalDate.of(2021, 2, 1))
      .build();

  @Autowired
  private TimetableHearingYearRepository timetableHearingYearRepository;

  @Autowired
  private TimetableHearingYearController timetableHearingYearController;

  @AfterEach
  void tearDown() {
    timetableHearingYearRepository.deleteAll();
  }

  @Test
  void shouldCreateHearingYear() throws Exception {
    mvc.perform(post("/v1/timetable-hearing/years")
            .contentType(contentType)
            .content(mapper.writeValueAsString(TIMETABLE_HEARING_YEAR)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$." + Fields.hearingStatus, is(HearingStatus.PLANNED.toString())));
  }

  @Test
  void shouldGetHearingYear() throws Exception {
    timetableHearingYearController.createHearingYear(TIMETABLE_HEARING_YEAR);

    mvc.perform(get("/v1/timetable-hearing/years/" + YEAR))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$." + Fields.hearingStatus, is(HearingStatus.PLANNED.toString())));
  }

  @Test
  void shouldGetHearingYearByStatus() throws Exception {
    timetableHearingYearController.createHearingYear(TIMETABLE_HEARING_YEAR);

    mvc.perform(get("/v1/timetable-hearing/years?statusChoices=" + HearingStatus.PLANNED))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));

    mvc.perform(get("/v1/timetable-hearing/years?statusChoices=" + HearingStatus.ACTIVE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void shouldStartHearingYear() throws Exception {
    timetableHearingYearController.createHearingYear(TIMETABLE_HEARING_YEAR);

    mvc.perform(post("/v1/timetable-hearing/years/" + YEAR + "/start"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$." + Fields.hearingStatus, is(HearingStatus.ACTIVE.toString())));
  }

  @Test
  void shouldUpdateSettingsOfHearingYear() throws Exception {
    TimetableHearingYearModel hearingYear = timetableHearingYearController.createHearingYear(TIMETABLE_HEARING_YEAR);
    hearingYear.setStatementCreatableExternal(false);

    mvc.perform(put("/v1/timetable-hearing/years/" + YEAR)
            .contentType(contentType)
            .content(mapper.writeValueAsString(hearingYear)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$." + Fields.hearingStatus, is(HearingStatus.PLANNED.toString())))
        .andExpect(jsonPath("$." + Fields.statementCreatableExternal, is(false)));
  }

  @Test
  void shouldCloseHearingYear() throws Exception {
    timetableHearingYearController.createHearingYear(TIMETABLE_HEARING_YEAR);
    timetableHearingYearController.startHearingYear(YEAR);

    mvc.perform(post("/v1/timetable-hearing/years/" + YEAR + "/close"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$." + Fields.hearingStatus, is(HearingStatus.ARCHIVED.toString())));
  }
}