package ch.sbb.timetable.field.number.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.timetable.field.number.service.VersionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(VersionController.class)
public class VersionControllerExceptionHandlingTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private VersionService versionService;

  @WithMockUser
  @Test
  void shouldReturnBadRequestExceptionOnInvalidSortParam() throws Exception {
    // Given
    when(versionService.getOverview(any(Pageable.class))).thenThrow(mock(PropertyReferenceException.class));
    // When
    // Then
    this.mockMvc.perform(get("/v1/field-numbers")
            .queryParam("page", "0")
            .queryParam("size", "5")
            .queryParam("sort", "nam,asc"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Pageable sort parameter is not valid."))
        .andExpect(jsonPath("$.error").value("Bad Request"));
  }

}
