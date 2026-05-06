package ch.sbb.line.directory.module.tth.controller;

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
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import ch.sbb.line.directory.module.tth.entity.TimetableHearingYear;
import ch.sbb.line.directory.module.tth.mapper.TimeTableHearingYearMapper;
import ch.sbb.line.directory.module.tth.repository.TimetableHearingYearRepository;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

class TimetableHearingYearControllerInternalApiTest extends BaseControllerApiTest {

  private static final long YEAR = 2022L;
  private static final TimetableHearingYearModel TIMETABLE_HEARING_YEAR = TimetableHearingYearModel.builder()
      .timetableYear(YEAR)
      .hearingFrom(LocalDate.of(2021, 1, 1))
      .hearingTo(LocalDate.of(2021, 2, 1))
      .build();

  @Autowired
  private TimetableHearingYearRepository timetableHearingYearRepository;

  private TimetableHearingYear timetableHearingYear;


  @BeforeEach
  void setUp() {
    timetableHearingYear = timetableHearingYearRepository.saveAndFlush(TimetableHearingYear.builder()
        .timetableYear(YEAR)
        .hearingStatus(HearingStatus.PLANNED)
        .hearingFrom(LocalDate.of(2021, 1, 1))
        .hearingTo(LocalDate.of(2021, 2, 1))
        .statementCreatableExternal(true)
        .statementCreatableInternal(true)
        .statementEditable(true)
        .build());
  }

  @AfterEach
  void tearDown() {
    timetableHearingYearRepository.deleteAll();
  }

  @Nested
  @DisplayName("GET internal/timetable-hearing/years")
  class GetHearingYears {

    @Test
    void shouldGetHearingYearsByStatus() throws Exception {
      mvc.perform(get("/internal/timetable-hearing/years?statusChoices=" + HearingStatus.PLANNED))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(1)));

      mvc.perform(get("/internal/timetable-hearing/years?statusChoices=" + HearingStatus.ACTIVE))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotGetHearingYearsAsUnauthorized() throws Exception {
      mvc.perform(get("/internal/timetable-hearing/years"))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("GET internal/timetable-hearing/years/{year}")
  class GetHearingYear {

    @Test
    void shouldGetHearingYear() throws Exception {
      mvc.perform(get("/internal/timetable-hearing/years/" + YEAR))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$." + Fields.hearingStatus, is(HearingStatus.PLANNED.toString())));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotGetHearingYearAsUnauthorized() throws Exception {
      mvc.perform(get("/internal/timetable-hearing/years/" + YEAR))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("POST internal/timetable-hearing/years")
  class CreateHearingYear {

    @Test
    void shouldCreateHearingYear() throws Exception {
      createHearingYear()
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$." + Fields.hearingStatus, is(HearingStatus.PLANNED.toString())));
    }

    private ResultActions createHearingYear() throws Exception {
      timetableHearingYearRepository.deleteAll();

      return mvc.perform(post("/internal/timetable-hearing/years")
          .contentType(contentType)
          .content(mapper.writeValueAsString(TIMETABLE_HEARING_YEAR)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotCreateHearingYearAsUnauthorized() throws Exception {
      createHearingYear().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotCreateHearingYearAsStandardUser() throws Exception {
      createHearingYear().andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("POST internal/timetable-hearing/years/{year}/start")
  class StartHearingYear {

    @Test
    void shouldStartHearingYear() throws Exception {
      mvc.perform(post("/internal/timetable-hearing/years/" + YEAR + "/start"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$." + Fields.hearingStatus, is(HearingStatus.ACTIVE.toString())));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotStartHearingYearAsUnauthorized() throws Exception {
      mvc.perform(post("/internal/timetable-hearing/years/" + YEAR + "/start"))
          .andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotStartHearingYearAsStandardUser() throws Exception {
      mvc.perform(post("/internal/timetable-hearing/years/" + YEAR + "/start"))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("PUT internal/timetable-hearing/years/{year}")
  class UpdateTimetableHearingSettings {

    @Test
    void shouldUpdateSettingsOfHearingYear() throws Exception {
      updateSettingsOfHearingYear()
          .andExpect(status().isOk())
          .andExpect(jsonPath("$." + Fields.hearingStatus, is(HearingStatus.PLANNED.toString())))
          .andExpect(jsonPath("$." + Fields.statementCreatableExternal, is(false)));
    }

    private ResultActions updateSettingsOfHearingYear() throws Exception {
      TimetableHearingYearModel hearingYear = TimeTableHearingYearMapper.toModel(timetableHearingYear);
      hearingYear.setStatementCreatableExternal(false);

      return mvc.perform(put("/internal/timetable-hearing/years/" + YEAR)
          .contentType(contentType)
          .content(mapper.writeValueAsString(hearingYear)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotUpdateHearingYearAsUnauthorized() throws Exception {
      updateSettingsOfHearingYear().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotUpdateHearingYearAsStandardUser() throws Exception {
      updateSettingsOfHearingYear().andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("POST internal/timetable-hearing/years/{year}/close")
  class CloseTimetableHearing {

    @Test
    void shouldCloseHearingYear() throws Exception {
      closeHearingYear().andExpect(status().isOk());
    }

    private ResultActions closeHearingYear() throws Exception {
      timetableHearingYearRepository.deleteAll();
      timetableHearingYearRepository.saveAndFlush(TimetableHearingYear.builder()
          .timetableYear(YEAR)
          .hearingStatus(HearingStatus.ACTIVE)
          .hearingFrom(LocalDate.of(2021, 1, 1))
          .hearingTo(LocalDate.of(2021, 2, 1))
          .statementCreatableExternal(true)
          .statementCreatableInternal(true)
          .statementEditable(true)
          .build());

      return mvc.perform(post("/internal/timetable-hearing/years/" + YEAR + "/close")
          .contentType(contentType)
          .content(mapper.writeValueAsString(Collections.emptyList())));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldNotCloseHearingYearAsUnauthorized() throws Exception {
      closeHearingYear().andExpect(status().isForbidden());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.STANDARD)
    void shouldNotCloseHearingYearAsStandardUser() throws Exception {
      closeHearingYear().andExpect(status().isForbidden());
    }
  }
}
