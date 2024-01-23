package ch.sbb.atlas.user.administration.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.user.administration.UserDisplayNameModel;
import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.user.administration.entity.ClientCredentialPermission;
import ch.sbb.atlas.user.administration.entity.PermissionRestriction;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import ch.sbb.atlas.user.administration.exception.RestrictionWithoutTypeException;
import ch.sbb.atlas.user.administration.service.ClientCredentialAdministrationService;
import ch.sbb.atlas.user.administration.service.GraphApiService;
import ch.sbb.atlas.user.administration.service.UserAdministrationService;
import ch.sbb.atlas.user.administration.service.UserPermissionDistributor;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

class UserAdministrationControllerTest {

  @Mock
  private UserAdministrationService userAdministrationService;

  @Mock
  private ClientCredentialAdministrationService clientCredentialAdministrationService;

  @Mock
  private UserPermissionDistributor userPermissionDistributor;

  @Mock
  private GraphApiService graphApiService;

  @Captor
  private ArgumentCaptor<UserAdministrationModel> userAdministrationModelArgumentCaptor;

  private UserAdministrationController userAdministrationController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    userAdministrationController = new UserAdministrationController(userAdministrationService,
        clientCredentialAdministrationService, userPermissionDistributor,
        graphApiService);

    when(userAdministrationService.getUserPage(any(), any(), any(), any())).thenReturn(Page.empty());
  }

  @Test
  void shouldReturnBadRequestOnPermissionSearchWithoutType() {
    ThrowingCallable function = () -> userAdministrationController.getUsers(Pageable.ofSize(5), Set.of("value"), null,
        Set.of(ApplicationType.LIDI));
    assertThatThrownBy(function).isInstanceOf(RestrictionWithoutTypeException.class);
  }

  @Test
  void shouldNotThrowExceptionOnPermissionSearchWithoutType() {
    assertThatNoException().isThrownBy(() -> userAdministrationController.getUsers(Pageable.ofSize(5), Set.of("value"),
        PermissionRestrictionType.BUSINESS_ORGANISATION,
        Set.of(ApplicationType.LIDI)));
  }

  @Test
  void shouldNotCallGraphApiOnNoUserIds() {
    List<UserDisplayNameModel> result = userAdministrationController.getUserInformation(Collections.emptyList());

    assertThat(result).isEmpty();
    verifyNoInteractions(graphApiService);
  }

  @Test
  void shouldSyncClientsAndUsersToKafka() {
    when(clientCredentialAdministrationService.getClientCredentialPermissions()).thenReturn(List.of(
        ClientCredentialPermission.builder()
            .clientCredentialId("1234-12453-13421345-11")
            .alias("ClientCreds")
            .comment("Aare bädele isch agseit")
            .application(ApplicationType.LIDI)
            .role(ApplicationRole.WRITER)
            .permissionRestrictions(Set.of(PermissionRestriction.builder()
                    .restriction("sboid")
                    .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
                .build()))
            .build()));

    String sbbuid = "u1234566";
    when(userAdministrationService.getAllUserIds()).thenReturn(List.of(sbbuid));
    when(graphApiService.resolveUsers(any())).thenReturn(List.of(UserModel.builder().sbbUserId(sbbuid).build()));
    when(userAdministrationService.getUserPermissions(any())).thenReturn(List.of(
        UserPermission.builder()
            .sbbUserId(sbbuid)
            .application(ApplicationType.TIMETABLE_HEARING)
            .role(ApplicationRole.WRITER)
            .permissionRestrictions(Set.of(PermissionRestriction.builder()
                .restriction("BERN")
                .type(PermissionRestrictionType.CANTON)
                .build()))
            .build()));

    userAdministrationController.syncPermissions();

    verify(userPermissionDistributor, times(2)).pushUserPermissionToKafka(userAdministrationModelArgumentCaptor.capture());

    UserAdministrationModel sentClientCredential = userAdministrationModelArgumentCaptor.getAllValues().get(0);
    assertThat(sentClientCredential.getUserId()).isEqualTo("1234-12453-13421345-11");
    assertThat(sentClientCredential.getPermissions()).hasSize(1);
    assertThat(sentClientCredential.getPermissions().iterator().next().getRestrictions()).hasSize(1);

    UserAdministrationModel sentUserModel = userAdministrationModelArgumentCaptor.getAllValues().get(1);
    assertThat(sentUserModel.getUserId()).isEqualTo(sbbuid);
    assertThat(sentUserModel.getPermissions()).hasSize(1);
    assertThat(sentUserModel.getPermissions().iterator().next().getRestrictions()).hasSize(1);
  }
}