package ch.sbb.atlas.user.administration.controller;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.user.administration.enumeration.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.exception.RestrictionWithoutTypeException;
import ch.sbb.atlas.user.administration.service.ClientCredentialAdministrationService;
import ch.sbb.atlas.user.administration.service.GraphApiService;
import ch.sbb.atlas.user.administration.service.UserAdministrationService;
import ch.sbb.atlas.user.administration.service.UserPermissionDistributor;
import java.util.Set;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
}