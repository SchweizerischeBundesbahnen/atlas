package ch.sbb.mail.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.mail.MailApiInternal;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithMockJwtAuthentication.MockRole;
import ch.sbb.mail.service.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@EnableAutoConfiguration
@AutoConfigureMockMvc
class MailControllerInternalTest {

  private final MockMvc mockMvc;
  
  @MockitoBean
  private MailService mailService;

  @Autowired
  MailControllerInternalTest(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  @Test
  @WithMockJwtAuthentication
  void shouldSendMailForAdmin() throws Exception {
    mockMvc.perform(mailRequest())
        .andExpect(status().isNoContent());

    verify(mailService).sendEmailWithHtmlTemplate(any());
  }

  @Test
  @WithMockJwtAuthentication
  void shouldReturnInternalServerErrorWhenSendingMailFails() throws Exception {
    doThrow(new RuntimeException("Mail sending failed"))
        .when(mailService).sendEmailWithHtmlTemplate(any());

    mockMvc.perform(mailRequest())
        .andExpect(status().isInternalServerError());

    verify(mailService).sendEmailWithHtmlTemplate(any());
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.STANDARD)
  void shouldRejectNonAdmin() throws Exception {
    mockMvc.perform(mailRequest())
        .andExpect(status().isForbidden());

    verifyNoInteractions(mailService);
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.UNAUTHORIZED)
  void shouldRejectUnauthorizedRole() throws Exception {
    mockMvc.perform(mailRequest())
        .andExpect(status().isForbidden());

    verifyNoInteractions(mailService);
  }

  @Test
  @WithMockJwtAuthentication(role = MockRole.NONE)
  void shouldRejectMissingRole() throws Exception {
    mockMvc.perform(mailRequest())
        .andExpect(status().isForbidden());

    verifyNoInteractions(mailService);
  }

  @Test
  void shouldRejectMissingToken() throws Exception {
    mockMvc.perform(mailRequest())
        .andExpect(status().isUnauthorized());

    verifyNoInteractions(mailService);
  }

  @Test
  @WithMockJwtAuthentication
  void shouldRejectMissingRequestId() throws Exception {
    mockMvc.perform(post("/internal/send")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mailNotification()))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(mailService);
  }

  private static MockHttpServletRequestBuilder mailRequest() {
    return post("/internal/send")
        .header(MailApiInternal.MAIL_REQUEST_ID_HEADER, "mail-request-id")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mailNotification());
  }

  private static String mailNotification() {
    return """
        {
          "to": ["receiver@example.com"],
          "subject": "Subject",
          "content": "Content"
        }
        """;
  }

}
