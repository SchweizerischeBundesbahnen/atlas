package ch.sbb.atlas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.configuration.Role;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

class UserServiceTest {

  @Test
  void shouldReturnSbbUidFromStringPrincipal() {
    //given
    Authentication authentication = Mockito.mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn("User");

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    //when
    String result = UserService.getUserIdentifier();

    //then
    assertThat(result).isNotNull().isEqualTo("User");
  }

  @Test
  void shouldReturnSbbUidFromJwtPrincipal() {
    //given
    Authentication authentication = Mockito.mock(Authentication.class);
    Jwt jwt = Mockito.mock(Jwt.class);
    when(jwt.getClaimAsString(UserService.SBBUID_CLAIM)).thenReturn("Ciao");
    when(authentication.getPrincipal()).thenReturn(jwt);

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    //when
    String result = UserService.getUserIdentifier();
    //then
    assertThat(result).isNotNull().isEqualTo("Ciao");
  }

  @Test
  void shouldThrowExceptionWithWrongSecurityContext() {
    //given
    Authentication authentication = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);

    SecurityContextHolder.setContext(securityContext);
    //when
    assertThrows(IllegalStateException.class, UserService::getUserIdentifier);
  }

  @Test
  void shouldReturnRoles() {
    //given
    Authentication authentication = Mockito.mock(Authentication.class);
    Jwt jwt = Mockito.mock(Jwt.class);
    when(jwt.getClaim(Role.ROLES_JWT_KEY)).thenReturn(List.of("role1", "role2", "role3"));
    when(authentication.getPrincipal()).thenReturn(jwt);

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    //when
    List<String> result = UserService.getRoles();
    //then
    assertThat(result).isNotNull().hasSize(3).contains("role1", "role2", "role3");
  }

  @Test
  void shouldReturnClientCredentialIdFromJwtPrincipal() {
    //given
    Authentication authentication = Mockito.mock(Authentication.class);
    Jwt jwt = Mockito.mock(Jwt.class);
    when(jwt.getClaimAsString(UserService.SBBUID_CLAIM)).thenReturn(null);
    when(jwt.getClaimAsString(UserService.AZP_CLAIM)).thenReturn("client_id");
    when(authentication.getPrincipal()).thenReturn(jwt);

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    //when
    String result = UserService.getUserIdentifier();
    //then
    assertThat(result).isNotNull().isEqualTo("client_id");
  }

}