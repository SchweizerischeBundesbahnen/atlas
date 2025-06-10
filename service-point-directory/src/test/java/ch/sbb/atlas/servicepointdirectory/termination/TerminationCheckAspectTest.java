package ch.sbb.atlas.servicepointdirectory.termination;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.TerminationInProgressException;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointValidationService;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUser;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUserParameter;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TerminationCheckAspectTest {

  @InjectMocks
  @Spy
  private TerminationCheckAspect aspect;

  @Mock
  private ServicePointValidationService validationService;

  @Test
  void shouldNotThrowExceptionWhenServicePointIsNotTerminationInProgress() throws Throwable {
    //given
    ServicePointVersion bern = ServicePointTestData.getBern();
    ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
    MethodSignature methodSignature = mock(MethodSignature.class);
    when(joinPoint.getSignature()).thenReturn(methodSignature);
    when(methodSignature.getMethod()).thenReturn(foo(bern));
    ServicePointVersion[] args = {bern};
    when(joinPoint.getArgs()).thenReturn(args);
    doCallRealMethod().when(validationService).checkIfServicePointIsTerminationInProgress(bern);

    //when
    aspect.checkTermination(joinPoint);
    
    //then
    verify(joinPoint).proceed();
  }

  @Test
  void shouldThrowExceptionWhenServicePointIfTerminationInProgress() {
    //given
    ServicePointVersion bern = ServicePointTestData.getBern();
    bern.setTerminationInProgress(true);
    ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
    MethodSignature methodSignature = mock(MethodSignature.class);
    when(joinPoint.getSignature()).thenReturn(methodSignature);
    when(methodSignature.getMethod()).thenReturn(foo(bern));
    ServicePointVersion[] args = {bern};
    when(joinPoint.getArgs()).thenReturn(args);
    doCallRealMethod().when(validationService).checkIfServicePointIsTerminationInProgress(bern);

    //when & then
    assertThrows(TerminationInProgressException.class, () -> aspect.checkTermination(joinPoint));
  }

  @Test
  void shouldThrowExceptionWhenParameterIsNotServicePoint() {
    //given
    ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
    MethodSignature methodSignature = mock(MethodSignature.class);
    when(joinPoint.getSignature()).thenReturn(methodSignature);
    when(methodSignature.getMethod()).thenReturn(fooWithWrongType(true));
    Boolean[] args = {true};
    when(joinPoint.getArgs()).thenReturn(args);

    //when & then
    assertThrows(IllegalStateException.class, () -> aspect.checkTermination(joinPoint));
  }

  @Test
  void shouldNotResolveUserWhenMethodParameterIsNotAnnotatedWithRunAsUserParameter() {
    //given
    ServicePointVersion bern = ServicePointTestData.getBern();
    ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
    MethodSignature methodSignature = mock(MethodSignature.class);
    when(joinPoint.getSignature()).thenReturn(methodSignature);
    when(methodSignature.getMethod()).thenReturn(fooWithoutRunAsUserParameter(bern));
    ServicePointVersion[] args = {bern};
    when(joinPoint.getArgs()).thenReturn(args);

    //when & then
    assertThrows(IllegalStateException.class, () -> aspect.getServicePointVersionParameter(joinPoint));
  }

  @TerminationCheck
  public Method foo(@TerminationCheckParameter ServicePointVersion servicePointVersion) {
    return Arrays.stream(getClass().getMethods()).filter(method -> method.getName().equals("foo")).findFirst().orElseThrow();
  }

  @RunAsUser
  public Method fooWithWrongType(@RunAsUserParameter boolean userName) {
    return Arrays.stream(getClass().getMethods()).filter(method -> method.getName().equals("fooWithWrongType")).findFirst()
        .orElseThrow();
  }

  @RunAsUser
  public Method fooWithoutRunAsUserParameter(ServicePointVersion servicePointVersion) {
    return Arrays.stream(getClass().getMethods()).filter(method -> method.getName().equals("fooWithoutRunAsUserParameter"))
        .findFirst().orElseThrow();
  }

}