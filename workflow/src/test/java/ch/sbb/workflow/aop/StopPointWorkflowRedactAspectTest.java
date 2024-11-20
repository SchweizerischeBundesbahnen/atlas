package ch.sbb.workflow.aop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.configuration.Role;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.user.administration.security.service.BusinessOrganisationBasedUserAdministrationService;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.Decision;
import ch.sbb.workflow.entity.JudgementType;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.repository.StopPointWorkflowRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

class StopPointWorkflowRedactAspectTest {

  public static final String SBOID = "ch:1:sboid:666";
  private RedactAspect redactAspect;

  @Mock
  private ProceedingJoinPoint joinPoint;

  @Mock
  private BusinessOrganisationBasedUserAdministrationService businessOrganisationBasedUserAdministrationService;

  @Mock
  private StopPointWorkflowRepository stopPointWorkflowRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    redactAspect = new RedactAspect(businessOrganisationBasedUserAdministrationService);
  }

  @Test
  void shouldRedactStopPointWorkflowWhenNotAuthorized() throws Throwable {
    //given
    Authentication authentication = Mockito.mock(Authentication.class);
    Jwt jwt = Mockito.mock(Jwt.class);
    when(jwt.getClaim(Role.ROLES_JWT_KEY)).thenReturn(List.of(Role.ATLAS_ROLES_UNAUTHORIZED_KEY));
    when(authentication.getPrincipal()).thenReturn(jwt);

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    doReturn(getStopPointWorkflow()).when(joinPoint).proceed();
    //when
    Object result = redactAspect.redact(joinPoint);
    //then
    assertThat(result).isNotNull();
    StopPointWorkflow stopPointWorkflow = (StopPointWorkflow) result;
    Person examinant = stopPointWorkflow.getExaminants().stream().toList().getFirst();
    assertThat(examinant.getMail()).isEqualTo("a*****");
    assertThat(examinant.getFirstName()).isEqualTo("M*****");
    assertThat(examinant.getLastName()).isEqualTo("H*****");
    assertThat(stopPointWorkflow.getApplicantMail()).isEqualTo("a*****");
    assertThat(stopPointWorkflow.getCcEmails().getFirst()).isEqualTo("a*****");
  }

  @Test
  void shouldRedactStopPointWorkflowWhenAuthorizedWithoutSboidPermission() throws Throwable {
    //given
    Authentication authentication = Mockito.mock(Authentication.class);
    Jwt jwt = Mockito.mock(Jwt.class);
    when(jwt.getClaim(Role.ROLES_JWT_KEY)).thenReturn(List.of("Role1"));
    when(authentication.getPrincipal()).thenReturn(jwt);

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    doReturn(getStopPointWorkflow()).when(joinPoint).proceed();
    //when
    Object result = redactAspect.redact(joinPoint);
    //then
    assertThat(result).isNotNull();
    StopPointWorkflow stopPointWorkflow = (StopPointWorkflow) result;
    Person examinant = stopPointWorkflow.getExaminants().stream().toList().getFirst();
    assertThat(examinant.getMail()).isEqualTo("a*****");
    assertThat(examinant.getFirstName()).isEqualTo("M*****");
    assertThat(examinant.getLastName()).isEqualTo("H*****");
    assertThat(stopPointWorkflow.getApplicantMail()).isEqualTo("a*****");
    assertThat(stopPointWorkflow.getCcEmails().getFirst()).isEqualTo("a*****");
  }

  @Test
  void shouldNotRedactStopPointWorkflowWhenAuthorizedWithSboidPermission() throws Throwable {
    //given
    Authentication authentication = Mockito.mock(Authentication.class);
    Jwt jwt = Mockito.mock(Jwt.class);
    when(jwt.getClaim(Role.ROLES_JWT_KEY)).thenReturn(List.of("Role1"));
    when(authentication.getPrincipal()).thenReturn(jwt);

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    doReturn(true).when(businessOrganisationBasedUserAdministrationService).hasUserPermissionsForBusinessOrganisation(SBOID,
        ApplicationType.SEPODI);

    doReturn(getStopPointWorkflow()).when(joinPoint).proceed();
    //when
    Object result = redactAspect.redact(joinPoint);
    //then
    assertThat(result).isNotNull();
    StopPointWorkflow stopPointWorkflow = (StopPointWorkflow) result;
    Person examinant = stopPointWorkflow.getExaminants().stream().toList().getFirst();
    assertThat(examinant.getMail()).isEqualTo("a@b.ch");
    assertThat(examinant.getFirstName()).isEqualTo("Marek");
    assertThat(examinant.getLastName()).isEqualTo("Hamsik");
    assertThat(stopPointWorkflow.getApplicantMail()).isEqualTo("asd@bc.ch");
    assertThat(stopPointWorkflow.getCcEmails().getFirst()).isEqualTo("asd@bc.ch");
  }

  @Test
  void shouldRedactStopPointWorkflowPageWhenNotAuthorized() throws Throwable {
    //given
    Authentication authentication = Mockito.mock(Authentication.class);
    Jwt jwt = Mockito.mock(Jwt.class);
    when(jwt.getClaim(Role.ROLES_JWT_KEY)).thenReturn(List.of(Role.ATLAS_ROLES_UNAUTHORIZED_KEY));
    when(authentication.getPrincipal()).thenReturn(jwt);

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    PageImpl<StopPointWorkflow> stopPointWorkflowPage = new PageImpl<>(List.of(getStopPointWorkflow()));

    doReturn(stopPointWorkflowPage).when(joinPoint).proceed();
    //when
    Object result = redactAspect.redact(joinPoint);
    //then
    assertThat(result).isNotNull();
    List<StopPointWorkflow> stopPointWorkflow = ((PageImpl<StopPointWorkflow>) result).stream().toList();
    Person examinant = stopPointWorkflow.getFirst().getExaminants().stream().toList().getFirst();
    assertThat(examinant.getMail()).isEqualTo("a*****");
    assertThat(examinant.getFirstName()).isEqualTo("M*****");
    assertThat(examinant.getLastName()).isEqualTo("H*****");
    assertThat(stopPointWorkflow.getFirst().getApplicantMail()).isEqualTo("a*****");
    assertThat(stopPointWorkflow.getFirst().getCcEmails().getFirst()).isEqualTo("a*****");
  }

  @Test
  void shouldRedactStopPointWorkflowPageWhenAuthorizedWithoutSboidPermission() throws Throwable {
    //given
    Authentication authentication = Mockito.mock(Authentication.class);
    Jwt jwt = Mockito.mock(Jwt.class);
    when(jwt.getClaim(Role.ROLES_JWT_KEY)).thenReturn(List.of("Role1"));
    when(authentication.getPrincipal()).thenReturn(jwt);

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    PageImpl<StopPointWorkflow> stopPointWorkflowPage = new PageImpl<>(List.of(getStopPointWorkflow()));

    doReturn(stopPointWorkflowPage).when(joinPoint).proceed();
    //when
    Object result = redactAspect.redact(joinPoint);
    //then
    assertThat(result).isNotNull();
    List<StopPointWorkflow> stopPointWorkflow = ((PageImpl<StopPointWorkflow>) result).stream().toList();
    Person examinant = stopPointWorkflow.getFirst().getExaminants().stream().toList().getFirst();
    assertThat(examinant.getMail()).isEqualTo("a*****");
    assertThat(examinant.getFirstName()).isEqualTo("M*****");
    assertThat(examinant.getLastName()).isEqualTo("H*****");
    assertThat(stopPointWorkflow.getFirst().getApplicantMail()).isEqualTo("a*****");
    assertThat(stopPointWorkflow.getFirst().getCcEmails().getFirst()).isEqualTo("a*****");
  }

  @Test
  void shouldRedactStopPointWorkflowPageWhenAuthorizedWithSboidPermission() throws Throwable {
    //given
    Authentication authentication = Mockito.mock(Authentication.class);
    Jwt jwt = Mockito.mock(Jwt.class);
    when(jwt.getClaim(Role.ROLES_JWT_KEY)).thenReturn(List.of("Role1"));
    when(authentication.getPrincipal()).thenReturn(jwt);

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    PageImpl<StopPointWorkflow> stopPointWorkflowPage = new PageImpl<>(List.of(getStopPointWorkflow()));
    doReturn(true).when(businessOrganisationBasedUserAdministrationService).hasUserPermissionsForBusinessOrganisation(SBOID,
        ApplicationType.SEPODI);
    doReturn(stopPointWorkflowPage).when(joinPoint).proceed();
    //when
    Object result = redactAspect.redact(joinPoint);
    //then
    assertThat(result).isNotNull();
    List<StopPointWorkflow> stopPointWorkflows = ((PageImpl<StopPointWorkflow>) result).stream().toList();
    Person examinant = stopPointWorkflows.getFirst().getExaminants().stream().toList().getFirst();
    assertThat(examinant.getMail()).isEqualTo("a@b.ch");
    assertThat(examinant.getFirstName()).isEqualTo("Marek");
    assertThat(examinant.getLastName()).isEqualTo("Hamsik");
    assertThat(stopPointWorkflows.getFirst().getApplicantMail()).isEqualTo("asd@bc.ch");
    assertThat(stopPointWorkflows.getFirst().getCcEmails().getFirst()).isEqualTo("asd@bc.ch");
  }

  @Test
  void shouldRedactOnlyStopPointWorkflowPageWhenAuthorizedWithSboidPermission() throws Throwable {
    //given
    Authentication authentication = Mockito.mock(Authentication.class);
    Jwt jwt = Mockito.mock(Jwt.class);
    when(jwt.getClaim(Role.ROLES_JWT_KEY)).thenReturn(List.of("Role1"));
    when(authentication.getPrincipal()).thenReturn(jwt);

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    PageImpl<StopPointWorkflow> stopPointWorkflowPage = new PageImpl<>(List.of(getStopPointWorkflow(), getStopPointWorkflow2()));
    doReturn(true).when(businessOrganisationBasedUserAdministrationService).hasUserPermissionsForBusinessOrganisation(SBOID,
        ApplicationType.SEPODI);
    doReturn(stopPointWorkflowPage).when(joinPoint).proceed();
    //when
    Object result = redactAspect.redact(joinPoint);
    //then
    assertThat(result).isNotNull();
    List<StopPointWorkflow> stopPointWorkflows = ((PageImpl<StopPointWorkflow>) result).stream().toList();
    StopPointWorkflow stopPointWorkflow1 = stopPointWorkflows.stream()
        .filter(stopPointWorkflow -> stopPointWorkflow.getId() == 1L).findFirst().orElseThrow(() -> new IdNotFoundException(1L));
    StopPointWorkflow stopPointWorkflow2 = stopPointWorkflows.stream()
        .filter(stopPointWorkflow -> stopPointWorkflow.getId() == 2L).findFirst().orElseThrow(() -> new IdNotFoundException(1L));
    Person examinant =stopPointWorkflow1.getExaminants().stream().toList().getFirst();
    assertThat(examinant.getMail()).isEqualTo("a@b.ch");
    assertThat(examinant.getFirstName()).isEqualTo("Marek");
    assertThat(examinant.getLastName()).isEqualTo("Hamsik");
    assertThat(stopPointWorkflow1.getApplicantMail()).isEqualTo("asd@bc.ch");
    assertThat(stopPointWorkflow1.getCcEmails().getFirst()).isEqualTo("asd@bc.ch");

    Person examinant1 = stopPointWorkflow2.getExaminants().stream().toList().getFirst();
    assertThat(examinant1.getMail()).isEqualTo("a*****");
    assertThat(examinant1.getFirstName()).isEqualTo("M*****");
    assertThat(examinant1.getLastName()).isEqualTo("H*****");
    assertThat(stopPointWorkflow2.getApplicantMail()).isEqualTo("a*****");
    assertThat(stopPointWorkflow2.getCcEmails().getFirst()).isEqualTo("a*****");
  }

  StopPointWorkflow getStopPointWorkflow() {
    Person person = Person.builder()
        .firstName("Marek")
        .lastName("Hamsik")
        .function("Centrocampista")
        .mail("a@b.ch").build();
    return StopPointWorkflow.builder()
        .id(1L)
        .sloid("ch:1:sloid:1234")
        .sboid(SBOID)
        .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
        .localityName("Biel/Bienne")
        .workflowComment("WF comment")
        .examinants(Set.of(person))
        .applicantMail("asd@bc.ch")
        .ccEmails(List.of("asd@bc.ch"))
        .startDate(LocalDate.of(2000, 1, 1))
        .endDate(LocalDate.of(2000, 12, 31))
        .versionId(1L)
        .status(WorkflowStatus.ADDED)
        .build();
  }

  StopPointWorkflow getStopPointWorkflow2() {
    Person person = Person.builder()
        .firstName("Marek")
        .lastName("Hamsik")
        .function("Centrocampista")
        .mail("a@b.ch").build();
    return StopPointWorkflow.builder()
        .id(2L)
        .sloid("ch:1:sloid:1234")
        .sboid("ch:1:sboid:123")
        .designationOfficial("Biel/Bienne Bözingenfeld/Champ")
        .localityName("Biel/Bienne")
        .workflowComment("WF comment")
        .examinants(Set.of(person))
        .applicantMail("asd@bc.ch")
        .ccEmails(List.of("asd@bc.ch"))
        .startDate(LocalDate.of(2000, 1, 1))
        .endDate(LocalDate.of(2000, 12, 31))
        .versionId(2L)
        .status(WorkflowStatus.ADDED)
        .build();
  }

  @Test
  void shouldRedactDecision() throws Throwable {
    //given
    Authentication authentication = Mockito.mock(Authentication.class);
    Jwt jwt = Mockito.mock(Jwt.class);
    when(jwt.getClaim(Role.ROLES_JWT_KEY)).thenReturn(List.of("Role1"));
    when(authentication.getPrincipal()).thenReturn(jwt);

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    doReturn(getStopPointWorkflow()).when(stopPointWorkflowRepository).findByDecisionId(any());

    doReturn(false).when(businessOrganisationBasedUserAdministrationService).hasUserPermissionsForBusinessOrganisation(SBOID,
        ApplicationType.SEPODI);
    doReturn(getDecision()).when(joinPoint).proceed();

    //when
    Object result = redactAspect.redact(joinPoint);
    //then
    assertThat(result).isNotNull();
    Decision decisionResult = (Decision) result;
    Person examinant = decisionResult.getExaminant();
    assertThat(examinant.getMail()).isEqualTo("b*****");
    assertThat(examinant.getFirstName()).isEqualTo("D*****");
    assertThat(examinant.getLastName()).isEqualTo("D*****");

    Person overrider = decisionResult.getFotOverrider();
    assertThat(overrider.getMail()).isEqualTo("j*****");
    assertThat(overrider.getFirstName()).isEqualTo("J*****");
    assertThat(overrider.getLastName()).isEqualTo("B*****");
  }

  @Test
  void shouldNotRedactDecision() throws Throwable {
    //given
    Authentication authentication = Mockito.mock(Authentication.class);
    Jwt jwt = Mockito.mock(Jwt.class);
    when(jwt.getClaim(Role.ROLES_JWT_KEY)).thenReturn(List.of("Role1"));
    when(authentication.getPrincipal()).thenReturn(jwt);

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    doReturn(getStopPointWorkflow()).when(stopPointWorkflowRepository).findByDecisionId(any());

    doReturn(true).when(businessOrganisationBasedUserAdministrationService).hasUserPermissionsForBusinessOrganisation(SBOID,
        ApplicationType.SEPODI);
    doReturn(getDecision()).when(joinPoint).proceed();

    //when
    Object result = redactAspect.redact(joinPoint);
    //then
    assertThat(result).isNotNull();
    Decision decisionResult = (Decision) result;
    Person examinant = decisionResult.getExaminant();
    assertThat(examinant.getMail()).isEqualTo("bro@gym.cc");
    assertThat(examinant.getFirstName()).isEqualTo("Danu");
    assertThat(examinant.getLastName()).isEqualTo("Djukic");

    Person overrider = decisionResult.getFotOverrider();
    assertThat(overrider.getMail()).isEqualTo("judiv@sbb.ch");
    assertThat(overrider.getFirstName()).isEqualTo("Judith");
    assertThat(overrider.getLastName()).isEqualTo("Brootwurscht");
  }

  private Decision getDecision(){
    Person daniel = Person.builder()
        .firstName("Danu")
        .lastName("Djukic")
        .function("Gymbro")
        .mail("bro@gym.cc")
        .build();
    Person judith = Person.builder()
        .firstName("Judith")
        .lastName("Brootwurscht")
        .function("atlas")
        .mail("judiv@sbb.ch")
        .build();
    return Decision.builder()
        .judgement(JudgementType.YES)
        .motivation("Good Name!")
        .motivationDate(LocalDateTime.now())
        .examinant(daniel)
        .fotJudgement(JudgementType.NO)
        .fotMotivation("No, is no!")
        .fotOverrider(judith)
        .build();
  }

}