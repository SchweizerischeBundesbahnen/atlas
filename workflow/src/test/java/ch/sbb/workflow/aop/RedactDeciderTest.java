package ch.sbb.workflow.aop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.security.service.BusinessOrganisationBasedUserAdministrationService;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class RedactDeciderTest {

  private static final String SBOID = "sboid";

  @Mock
  private ProceedingJoinPoint joinPoint;

  @Mock
  private MethodSignature methodSignature;

  @Mock
  private BusinessOrganisationBasedUserAdministrationService businessOrganisationBasedUserAdministrationService;

  private RedactDecider redactDecider;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    redactDecider = new RedactDecider(businessOrganisationBasedUserAdministrationService);

    when(joinPoint.getSignature()).thenReturn(methodSignature);
  }

  @Test
  void shouldRedactWhenNotAuthorized() {
    //given
    when(methodSignature.getMethod()).thenReturn(getMethodByName("getExampleWithImplicitSboid"));
    when(joinPoint.getArgs()).thenReturn(new Object[]{1L});

    MockSecurityContext.setSecurityContextToUnauthorized();

    //when
    boolean shouldRedact = redactDecider.shouldRedact(joinPoint, getExampleWithImplicitSboid(1L));

    //then
    assertThat(shouldRedact).isTrue();
    verify(businessOrganisationBasedUserAdministrationService)
        .hasUserPermissionsForBusinessOrganisation(SBOID, ApplicationType.SEPODI);
  }

  @Test
  void shouldRedactWhenAuthorizedWithoutSboidPermission() {
    //given
    when(methodSignature.getMethod()).thenReturn(getMethodByName("getExampleWithImplicitSboid"));
    when(joinPoint.getArgs()).thenReturn(new Object[]{1L});

    MockSecurityContext.setSecurityContextToAuthorized();

    //when
    boolean shouldRedact = redactDecider.shouldRedact(joinPoint, getExampleWithImplicitSboid(1L));
    //then
    assertThat(shouldRedact).isTrue();
    verify(businessOrganisationBasedUserAdministrationService)
        .hasUserPermissionsForBusinessOrganisation(SBOID, ApplicationType.SEPODI);
  }

  @Test
  void shouldNotRedactWhenAuthorizedWithSboidPermission() {
    //given
    when(methodSignature.getMethod()).thenReturn(getMethodByName("getExampleWithImplicitSboid"));
    when(joinPoint.getArgs()).thenReturn(new Object[]{1L});

    MockSecurityContext.setSecurityContextToAuthorized();
    doReturn(true).when(businessOrganisationBasedUserAdministrationService).hasUserPermissionsForBusinessOrganisation(SBOID,
        ApplicationType.SEPODI);

    //when
    boolean shouldRedact = redactDecider.shouldRedact(joinPoint, getExampleWithImplicitSboid(1L));

    //then
    assertThat(shouldRedact).isFalse();
    verify(businessOrganisationBasedUserAdministrationService)
        .hasUserPermissionsForBusinessOrganisation(SBOID, ApplicationType.SEPODI);
  }

  @Test
  void shouldNotRedactWhenAuthorizedWithSboidPermissionViaParameter() {
    //given
    when(methodSignature.getMethod()).thenReturn(getMethodByName("getExampleWithParameterSboid"));
    String sboidParameter = "sboidParameter";
    when(joinPoint.getArgs()).thenReturn(new Object[]{1L, sboidParameter});

    MockSecurityContext.setSecurityContextToAuthorized();
    doReturn(true).when(businessOrganisationBasedUserAdministrationService).hasUserPermissionsForBusinessOrganisation(sboidParameter,
        ApplicationType.SEPODI);

    //when
    boolean shouldRedact = redactDecider.shouldRedact(joinPoint, getExampleWithParameterSboid(1L, sboidParameter));

    //then
    assertThat(shouldRedact).isFalse();
    verify(businessOrganisationBasedUserAdministrationService)
        .hasUserPermissionsForBusinessOrganisation(sboidParameter, ApplicationType.SEPODI);
  }

  private Method getMethodByName(String methodName) {
    return Arrays.stream(getClass().getMethods())
        .filter(method -> method.getName().equals(methodName))
        .findFirst().orElseThrow();
  }

  @Redacted
  public RedactTarget getExampleWithImplicitSboid(Long id) {
    return RedactTarget.builder().sboid(SBOID).build();
  }

  @Redacted
  public RedactTarget getExampleWithParameterSboid(Long id, @RedactBySboid(application = ApplicationType.SEPODI) String sboid) {
    return RedactTarget.builder().build();
  }

}