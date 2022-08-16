package ch.sbb.line.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import org.junit.jupiter.api.Test;

public class FutureTimetableControllerApiTest extends BaseControllerApiTest {

  @Test
  public void shouldReturnFutureTimeTable() throws Exception {
    //given
    String year = "2022";
    //when
    mvc.perform(get("/v1/future-timetable/" + year))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$", is("2022-12-11")));
  }

  @Test
  public void shouldReturnErrorWhenYearBefore1700() throws Exception {
    //given
    String year = "1699";
    //when
    mvc.perform(get("/v1/future-timetable/" + year))
       .andExpect(status().isBadRequest())
       .andExpect(jsonPath("$.status", is(400)))
       .andExpect(jsonPath("$.error", is("Param argument not valid error")))
       .andExpect(jsonPath("$.message",
           is("Constraint for Path parameter was violated: [Path parameter 'year' value '1699' must be greater than or equal to 1700]")));
  }

  @Test
  public void shouldReturnErrorWhenYearAfter9999() throws Exception {
    //given
    String year = "10000";
    //when
    mvc.perform(get("/v1/future-timetable/" + year))
       .andExpect(status().isBadRequest())
       .andExpect(jsonPath("$.status", is(400)))
       .andExpect(jsonPath("$.error", is("Param argument not valid error")))
       .andExpect(jsonPath("$.message",
           is("Constraint for Path parameter was violated: [Path parameter 'year' value '10000' must be less than or equal to 9999]")));
  }

  @Test
  public void shouldReturnErrorWhenNextFutureTimeTablesIsZero() throws Exception {
    //given
    String count = "0";
    //when
    mvc.perform(get("/v1/future-timetable/next-years/" + count))
       .andExpect(status().isBadRequest())
       .andExpect(jsonPath("$.status", is(400)))
       .andExpect(jsonPath("$.error", is("Param argument not valid error")))
       .andExpect(jsonPath("$.message",
           is("Constraint for Path parameter was violated: [Path parameter 'count' value '0' must be greater than or equal to 1]")));
  }

  @Test
  public void shouldReturnErrorWhenNextFutureTimeTablesIsMoreThanHundred() throws Exception {
    //given
    String count = "101";
    //when
    mvc.perform(get("/v1/future-timetable/next-years/" + count))
       .andExpect(status().isBadRequest())
       .andExpect(jsonPath("$.status", is(400)))
       .andExpect(jsonPath("$.error", is("Param argument not valid error")))
       .andExpect(jsonPath("$.message",
           is("Constraint for Path parameter was violated: [Path parameter 'count' value '101' must be less than or equal to 100]")));
  }

  @Test
  public void shouldReturnNextFutureTimeTables() throws Exception {
    //given
    String count = "10";
    //when
    mvc.perform(get("/v1/future-timetable/next-years/" + count))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$", hasSize(10)))
       .andExpect(jsonPath("$[0]", is("2023-12-10")))
       .andExpect(jsonPath("$[1]", is("2024-12-15")))
       .andExpect(jsonPath("$[2]", is("2025-12-14")))
       .andExpect(jsonPath("$[3]", is("2026-12-13")))
       .andExpect(jsonPath("$[4]", is("2027-12-12")))
       .andExpect(jsonPath("$[5]", is("2028-12-10")))
       .andExpect(jsonPath("$[6]", is("2029-12-09")))
       .andExpect(jsonPath("$[7]", is("2030-12-15")))
       .andExpect(jsonPath("$[8]", is("2031-12-14")))
       .andExpect(jsonPath("$[9]", is("2032-12-12")));
  }

}