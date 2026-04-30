package ch.sbb.line.directory.module.tth.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TimetableYearChangeControllerInternalApiTest extends BaseControllerApiTest {

  @Nested
  @DisplayName("internal/timetable-year-change/{year}")
  class GetTimetableYearChange {

    @Test
    void shouldReturnFutureTimeTable() throws Exception {
      //given
      String year = "2022";
      //when
      mvc.perform(get("/internal/timetable-year-change/" + year))
          .andExpect(status().isOk());
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldReturnFutureTimeTableForUnauthorized() throws Exception {
      mvc.perform(get("/internal/timetable-year-change/2022"))
          .andExpect(status().isOk());
    }

    @Test
    void shouldReturnErrorWhenYearBefore1700() throws Exception {
      //given
      String year = "1699";
      //when
      mvc.perform(get("/internal/timetable-year-change/" + year))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status", is(400)))
          .andExpect(jsonPath("$.error", is("Constraint violation")))
          .andExpect(jsonPath("$.message",
              is("Following constraints were violated: [Property 'getTimetableYearChange.year' has invalid value: '1699']")));
    }

    @Test
    void shouldReturnErrorWhenYearAfter9999() throws Exception {
      //given
      String year = "10000";
      //when
      mvc.perform(get("/internal/timetable-year-change/" + year))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status", is(400)))
          .andExpect(jsonPath("$.error", is("Constraint violation")))
          .andExpect(jsonPath("$.message",
              is("Following constraints were violated: [Property 'getTimetableYearChange.year' has invalid value: '10000']")));
    }
  }

  @Nested
  @DisplayName("internal/timetable-year-change/next-years/{count}")
  class GetNextTimetablesYearChange {

    @Test
    void shouldReturnErrorWhenNextFutureTimeTablesIsZero() throws Exception {
      //given
      String count = "0";
      //when
      mvc.perform(get("/internal/timetable-year-change/next-years/" + count))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status", is(400)))
          .andExpect(jsonPath("$.error", is("Constraint violation")))
          .andExpect(jsonPath("$.message",
              is("Following constraints were violated: [Property 'getNextTimetablesYearChange.count' has invalid value: '0']")));
    }

    @Test
    void shouldReturnErrorWhenNextFutureTimeTablesIsMoreThanHundred() throws Exception {
      //given
      String count = "101";
      //when
      mvc.perform(get("/internal/timetable-year-change/next-years/" + count))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status", is(400)))
          .andExpect(jsonPath("$.error", is("Constraint violation")))
          .andExpect(jsonPath("$.message",
              is("Following constraints were violated: [Property 'getNextTimetablesYearChange.count' has invalid value: '101']")));
    }

    @Test
    void shouldReturnNextFutureTimeTables() throws Exception {
      //given
      String count = "10";
      //when
      mvc.perform(get("/internal/timetable-year-change/next-years/" + count))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(10)));
    }

    @Test
    @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
    void shouldReturnNextFutureTimeTablesUnauthorized() throws Exception {
      mvc.perform(get("/internal/timetable-year-change/next-years/10"))
          .andExpect(status().isOk());
    }
  }

}
