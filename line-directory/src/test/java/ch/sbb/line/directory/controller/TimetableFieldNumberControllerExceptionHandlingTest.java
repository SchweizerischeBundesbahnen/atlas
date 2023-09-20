package ch.sbb.line.directory.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.model.search.TimetableFieldNumberSearchRestrictions;
import ch.sbb.line.directory.service.TimetableFieldNumberService;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.util.TypeInformation;
import org.springframework.security.test.context.support.WithMockUser;

 class TimetableFieldNumberControllerExceptionHandlingTest extends BaseControllerApiTest {

  @MockBean
  private TimetableFieldNumberService timetableFieldNumberService;

  @WithMockUser
  @Test
  void shouldReturnBadRequestExceptionOnInvalidSortParam() throws Exception {
    // Given
    when(timetableFieldNumberService.getVersionsSearched(any(
        TimetableFieldNumberSearchRestrictions.class))).thenThrow(
        new PropertyReferenceException("nam",
            TypeInformation.of(TimetableFieldNumber.class), Collections.emptyList()));
    // When
    // Then
    mvc.perform(get("/v1/field-numbers")
            .queryParam("page", "0")
            .queryParam("size", "5")
            .queryParam("sort", "nam,asc"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value(
            "Supplied sort field nam not found on TimetableFieldNumber"));
  }

}
