package ch.sbb.mail.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import ch.sbb.atlas.api.mail.MailApi;
import ch.sbb.atlas.configuration.Role;
import ch.sbb.mail.config.SecurityConfig;
import ch.sbb.mail.exception.MailSendException;
import ch.sbb.mail.service.MailService;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MailController.class)
@Import(SecurityConfig.class)
class MailControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private MailService mailService;

  @MockitoBean
  private JwtDecoder jwtDecoder;

  @Test
  void shouldSendMailForAdmin() throws Exception {
    mockMvc.perform(post("/internal/mail")
            .with(jwt().authorities(new SimpleGrantedAuthority(Role.SECURED_FOR_ATLAS_ADMIN)))
            .header(MailApi.MAIL_REQUEST_ID_HEADER, "mail-request-id")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "to": ["receiver@example.com"],
                  "subject": "Subject",
                  "content": "Content"
                }
                """))
        .andExpect(status().isNoContent());

    verify(mailService).sendEmailWithHtmlTemplate(any());
  }

  @Test
  void shouldLogMailRequestOutcome() throws Exception {
    Logger logger = (Logger) LoggerFactory.getLogger(MailController.class);
    Level previousLevel = logger.getLevel();
    ListAppender<ILoggingEvent> logAppender = new ListAppender<>();
    logAppender.start();
    logger.setLevel(Level.INFO);
    logger.addAppender(logAppender);

    try {
      mockMvc.perform(post("/internal/mail")
              .with(jwt().authorities(new SimpleGrantedAuthority(Role.SECURED_FOR_ATLAS_ADMIN)))
              .header(MailApi.MAIL_REQUEST_ID_HEADER, "successful-request")
              .contentType(MediaType.APPLICATION_JSON)
              .content("""
                  {
                    "to": ["receiver@example.com"],
                    "subject": "Subject",
                    "content": "Content"
                  }
                  """))
          .andExpect(status().isNoContent());

      doThrow(new MailSendException("SMTP unavailable", new RuntimeException()))
          .when(mailService).sendEmailWithHtmlTemplate(any());

      assertThatThrownBy(() -> mockMvc.perform(post("/internal/mail")
          .with(jwt().authorities(new SimpleGrantedAuthority(Role.SECURED_FOR_ATLAS_ADMIN)))
          .header(MailApi.MAIL_REQUEST_ID_HEADER, "failed-request")
          .contentType(MediaType.APPLICATION_JSON)
          .content("""
              {
                "to": ["receiver@example.com"],
                "subject": "Subject",
                "content": "Content"
              }
              """)))
          .hasCauseInstanceOf(MailSendException.class);

      assertThat(logAppender.list)
          .extracting(ILoggingEvent::getFormattedMessage)
          .contains(
              "mailReqId=successful-request, status=MAIL_SEND_SUCCESSED",
              "mailReqId=failed-request, status=MAIL_SEND_FAILED"
          );
    } finally {
      logger.detachAppender(logAppender);
      logger.setLevel(previousLevel);
      logAppender.stop();
    }
  }

  @Test
  void shouldRejectNonAdmin() throws Exception {
    mockMvc.perform(post("/internal/mail")
            .with(jwt().authorities(new SimpleGrantedAuthority(Role.AUTHORITY_INTERNAL)))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "to": ["receiver@example.com"],
                  "subject": "Subject",
                  "content": "Content"
                }
                """))
        .andExpect(status().isForbidden());

    verifyNoInteractions(mailService);
  }

  @Test
  void shouldRejectMissingToken() throws Exception {
    mockMvc.perform(post("/internal/mail")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "to": ["receiver@example.com"],
                  "subject": "Subject",
                  "content": "Content"
                }
                """))
        .andExpect(status().isUnauthorized());

    verifyNoInteractions(mailService);
  }

}
