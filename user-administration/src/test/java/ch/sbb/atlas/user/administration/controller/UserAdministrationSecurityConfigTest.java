package ch.sbb.atlas.user.administration.controller;

import static org.hamcrest.Matchers.not;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.model.controller.TestcontainersConfiguration;
import ch.sbb.atlas.model.controller.WithUnauthorizedMockJwtAuthentication;
import ch.sbb.atlas.model.controller.WithUnauthorizedMockJwtAuthentication.MockUnauthorizedJwtAuthenticationFactory;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@WithUnauthorizedMockJwtAuthentication
@ActiveProfiles("integration-test")
@Import(TestcontainersConfiguration.class)
@EmbeddedKafka
@AutoConfigureMockMvc
class UserAdministrationSecurityConfigTest {

  @Autowired
  private MockMvc mvc;

  @Test
  void shouldAllowDisplayNameQueryForUnauthorizedInternalRoleAndMaskResponse() throws Exception {
    Authentication authentication = new JwtAuthenticationToken(MockUnauthorizedJwtAuthenticationFactory.createJwt("u123456"),
        AuthorityUtils.createAuthorityList(Role.AUTHORITY_UNAUTHORIZED, Role.AUTHORITY_INTERNAL));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    mvc.perform(get("/v1/users/u236171/displayname"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").value("*****"));
  }

  @Test
  void shouldAllowDisplayNameQueryForAuthorizedInternalRoleAndNotMaskResponse() throws Exception {
    Jwt jwt = MockUnauthorizedJwtAuthenticationFactory.createJwt("u123456", List.of(Role.ATLAS_INTERNAL));
    Authentication authentication = new JwtAuthenticationToken(jwt, AuthorityUtils.createAuthorityList(Role.AUTHORITY_INTERNAL));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    mvc.perform(get("/v1/users/u236171/displayname"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").value(not("*****")));
  }

  @Test
  void shouldNotAllowDisplayNameQueryForOthersWithNoRoles() throws Exception {
    Jwt jwt = MockUnauthorizedJwtAuthenticationFactory.createJwt("u123456", List.of());
    Authentication authentication = new JwtAuthenticationToken(jwt, AuthorityUtils.createAuthorityList());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    mvc.perform(get("/v1/users/u236171/displayname"))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldNotAllowSearchToUnauthorizedInternal() throws Exception {
    Authentication authentication = new JwtAuthenticationToken(MockUnauthorizedJwtAuthenticationFactory.createJwt("u123456"),
        AuthorityUtils.createAuthorityList(Role.AUTHORITY_UNAUTHORIZED, Role.AUTHORITY_INTERNAL));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    mvc.perform(get("/v1/search").param("searchQuery", "testQuery"))
        .andExpect(status().isForbidden());
  }

}