package ch.sbb.atlas.base.service.model.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class UserServiceTest {

  @Test
  public void shouldReturnSbbUidFromStringPrincipal() {
    //given
    Authentication authentication = Mockito.mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn("User");
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    //when
    String result = UserService.getSbbUid();
    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("User");
  }

  @Test
  public void shouldReturnSbbUidFromJwtPrincipal() {
    //given
    Authentication authentication = Mockito.mock(Authentication.class);
    Jwt jwt = Mockito.mock(Jwt.class);
    when(jwt.getClaimAsString("sbbuid")).thenReturn("Ciao");
    when(authentication.getPrincipal()).thenReturn(jwt);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    //when
    String result = UserService.getSbbUid();
    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("Ciao");
  }

  @Test
  public void shouldThrowExceptionWhithWrongSecurityContext() {
    //given
    Authentication authentication = Mockito.mock(Authentication.class);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    //when
    assertThrows(IllegalStateException.class, UserService::getSbbUid);
  }

  @Test
  public void shouldReturnRoles() {
    //given
    Authentication authentication = Mockito.mock(Authentication.class);
    Jwt jwt = Mockito.mock(Jwt.class);
    when(jwt.getClaim("roles")).thenReturn(List.of("role1", "role2", "role3"));
    when(authentication.getPrincipal()).thenReturn(jwt);
    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    //when
    List<String> result = UserService.getRoles();
    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(3).contains("role1", "role2", "role3");
  }

}