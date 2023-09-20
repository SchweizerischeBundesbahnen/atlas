package ch.sbb.line.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import org.junit.jupiter.api.Test;

 class TimetableYearChangeControllerApiTest extends BaseControllerApiTest {

  @Test
   void shouldReturnFutureTimeTable() throws Exception {
    //given
    String year = "2022";
    //when
    mvc.perform(get("/v1/timetable-year-change/" + year))
        .andExpect(status().isOk());
  }

  @Test
   void shouldReturnErrorWhenYearBefore1700() throws Exception {
    //given
    String year = "1699";
    //when
    mvc.perform(get("/v1/timetable-year-change/" + year))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.error", is("Param argument not valid error")))
        .andExpect(jsonPath("$.message",
            is("Constraint for Path parameter was violated: [Path parameter 'year' value '1699' must be greater than or equal "
                + "to 1700]")));
  }

  @Test
   void shouldReturnErrorWhenYearAfter9999() throws Exception {
    //given
    String year = "10000";
    //when
    mvc.perform(get("/v1/timetable-year-change/" + year))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.error", is("Param argument not valid error")))
        .andExpect(jsonPath("$.message",
            is("Constraint for Path parameter was violated: [Path parameter 'year' value '10000' must be less than or equal to "
                + "9999]")));
  }

  @Test
   void shouldReturnErrorWhenNextFutureTimeTablesIsZero() throws Exception {
    //given
    String count = "0";
    //when
    mvc.perform(get("/v1/timetable-year-change/next-years/" + count))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.error", is("Param argument not valid error")))
        .andExpect(jsonPath("$.message",
            is("Constraint for Path parameter was violated: [Path parameter 'count' value '0' must be greater than or equal to "
                + "1]")));
  }

  @Test
   void shouldReturnErrorWhenNextFutureTimeTablesIsMoreThanHundred() throws Exception {
    //given
    String count = "101";
    //when
    mvc.perform(get("/v1/timetable-year-change/next-years/" + count))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.error", is("Param argument not valid error")))
        .andExpect(jsonPath("$.message",
            is("Constraint for Path parameter was violated: [Path parameter 'count' value '101' must be less than or equal to "
                + "100]")));
  }

  @Test
   void shouldReturnNextFutureTimeTables() throws Exception {
    //given
    String count = "10";
    //when
    mvc.perform(get("/v1/timetable-year-change/next-years/" + count))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(10)));
  }

}