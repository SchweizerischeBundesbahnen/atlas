package ch.sbb.timetable.field.number.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.timetable.field.number.entity.TimetableFieldNumber;
import ch.sbb.timetable.field.number.service.VersionService;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(VersionController.class)
@ActiveProfiles("integration-test")
public class VersionControllerExceptionHandlingTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private VersionService versionService;

  @WithMockUser
  @Test
  void shouldReturnBadRequestExceptionOnInvalidSortParam() throws Exception {
    // Given
    when(versionService.getVersionsSearched(any(Pageable.class), any(), any(), any())).thenThrow(new PropertyReferenceException( "nam",
        ClassTypeInformation.from(TimetableFieldNumber.class), Collections.emptyList()));
    // When
    // Then
    this.mockMvc.perform(get("/v1/field-numbers")
            .queryParam("page", "0")
            .queryParam("size", "5")
            .queryParam("sort", "nam,asc"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Supplied sort field nam not found on TimetableFieldNumber"));
  }

}
