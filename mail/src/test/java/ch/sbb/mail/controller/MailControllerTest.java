package ch.sbb.mail.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.configuration.Role;
import ch.sbb.mail.config.SecurityConfig;
import ch.sbb.mail.service.MailService;
import org.junit.jupiter.api.Test;
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
