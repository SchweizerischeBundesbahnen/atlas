package ch.sbb.line.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import org.junit.jupiter.api.Test;

class TimetableYearChangeControllerInternalApiTest extends BaseControllerApiTest {

  @Test
  void shouldReturnFutureTimeTable() throws Exception {
    //given
    String year = "2022";
    //when
    mvc.perform(get("/internal/timetable-year-change/" + year))
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

}
