package ch.sbb.line.directory.module.tth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.AtlasMockMultipartFile;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockUser;
import ch.sbb.line.directory.module.tth.exception.TimetableHearingMovedException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

class TimetableHearingStatementControllerV2ApiTest extends BaseControllerApiTest {

  private static final String EXTERNAL_PATH = "/v2/timetable-hearing/statements/external";

  @Test
  @WithMockJwtAuthentication(role = MockRole.ATLAS_ADMIN, user = MockUser.CLIENT_CREDENTIAL)
  void shouldRejectCreateStatementExternalAsMovedToTimetableHearing() throws Exception {
    // When / Then
    mvc.perform(multipart(HttpMethod.POST, EXTERNAL_PATH).file(statementJson()))
        .andExpect(status().isGone())
        .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(TimetableHearingMovedException.class))
        .andExpect(jsonPath("$.message").value(
            "This endpoint moved to /timetable-hearing/v2/timetable-hearing/statements/external"));
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
  void shouldDenyCreateStatementExternalForUnauthorizedUser() throws Exception {
    // When / Then
    mvc.perform(multipart(HttpMethod.POST, EXTERNAL_PATH).file(statementJson()))
        .andExpect(status().isForbidden());
  }

  private static MockMultipartFile statementJson() {
    final String statement = """
        {
          "statement": "I need some more busses please.",
          "statementSender": {
            "emails": ["maurer@post.ch"],
            "firstName": "Fabienne",
            "lastName": "Maurer",
            "organisation": "Post AG",
            "street": "Bahnhofstrasse 12",
            "zip": 3000,
            "city": "Bern"
          },
          "timetableFieldNumber": "1.1",
          "swissCanton": "BERN",
          "stopPlace": "Bern, Wyleregg"
        }
        """;
    return new AtlasMockMultipartFile("statement", null, MediaType.APPLICATION_JSON_VALUE, statement);
  }

}
