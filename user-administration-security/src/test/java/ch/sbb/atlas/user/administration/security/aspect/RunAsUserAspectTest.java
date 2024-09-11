package ch.sbb.atlas.user.administration.security.aspect;

import static ch.sbb.atlas.configuration.Role.ROLES_JWT_KEY;
import static ch.sbb.atlas.service.UserService.SBBUID_CLAIM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class RunAsUserAspectTest {

  @InjectMocks
  @Spy
  private RunAsUserAspect runAsUserAspect;

  @Test
  void shouldNotRunAsUserWhenNoUserProvided() throws Throwable {
    //given
    ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
    doReturn(null).when(runAsUserAspect).resolveRunAsUserParameter(joinPoint);

    //when & then
    assertThrows(IllegalStateException.class,
        () -> runAsUserAspect.logExecutionTime(joinPoint));

    verify(joinPoint,never()).proceed();
  }

  @Test
  void shouldRunAsUser() throws Throwable {
    //given
    Authentication authentication = Mockito.mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn("User");

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    Jwt jwt = Jwt.withTokenValue("token")
        .header("alg", "none")
        .claim(SBBUID_CLAIM,"e123456")
        .claim(ROLES_JWT_KEY, List.of("admin","ceo"))
        .claim("claim", "claim")
        .build();

    when(securityContext.getAuthentication().getPrincipal()).thenReturn(jwt);
    SecurityContextHolder.setContext(securityContext);
    ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);

    doReturn("e123456").when(runAsUserAspect).resolveRunAsUserParameter(joinPoint);
    //when
    runAsUserAspect.logExecutionTime(joinPoint);

    // then
    verify(joinPoint).proceed();
  }

  @Test
  void shouldResolveUser() throws Throwable {
    //given
    ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
    MethodSignature methodSignature = mock(MethodSignature.class);
    when(joinPoint.getSignature()).thenReturn(methodSignature);
    String userName = "e123456";
    when(methodSignature.getMethod()).thenReturn(foo(userName));
    when(joinPoint.getTarget()).thenReturn(new RunAsUserAspectTest());
    String[] args = {userName};
    when(joinPoint.getArgs()).thenReturn(args);

    //when
    String result = runAsUserAspect.resolveRunAsUserParameter(joinPoint);

    // then
    assertThat(result).isNotNull().isEqualTo(userName);
  }

  @Test
  void shouldNotResolveUserWhenMethodParameterIsNotAnnotatedWithRunAsUserParameter() {
    //given
    ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
    MethodSignature methodSignature = mock(MethodSignature.class);
    when(joinPoint.getSignature()).thenReturn(methodSignature);
    String userName = "e123456";
    when(methodSignature.getMethod()).thenReturn(fooWithoutRunAsUserParameter(userName));
    when(joinPoint.getTarget()).thenReturn(new RunAsUserAspectTest());
    String[] args = {userName};
    when(joinPoint.getArgs()).thenReturn(args);

    //when & then
    assertThrows(IllegalStateException.class, () -> runAsUserAspect.resolveRunAsUserParameter(joinPoint));
  }

  @Test
  void shouldNotResolveUserWhenMethodParameterIsString() {
    //given
    ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
    MethodSignature methodSignature = mock(MethodSignature.class);
    when(joinPoint.getSignature()).thenReturn(methodSignature);
    when(methodSignature.getMethod()).thenReturn(fooWithWrongType(true));
    when(joinPoint.getTarget()).thenReturn(new RunAsUserAspectTest());
    Boolean[] args = {true};
    when(joinPoint.getArgs()).thenReturn(args);

    //when & then
    assertThrows(IllegalStateException.class, () -> runAsUserAspect.resolveRunAsUserParameter(joinPoint));
  }

  @RunAsUser
  public Method foo(@RunAsUserParameter String userName) {
    return Arrays.stream(getClass().getMethods()).filter(method -> method.getName().equals("foo")).findFirst().orElseThrow();
  }

  @RunAsUser
  public Method fooWithWrongType(@RunAsUserParameter boolean userName) {
    return Arrays.stream(getClass().getMethods()).filter(method -> method.getName().equals("fooWithWrongType")).findFirst()
        .orElseThrow();
  }

  @RunAsUser
  public Method fooWithoutRunAsUserParameter(String userName) {
    return Arrays.stream(getClass().getMethods()).filter(method -> method.getName().equals("fooWithoutRunAsUserParameter"))
        .findFirst().orElseThrow();
  }
}