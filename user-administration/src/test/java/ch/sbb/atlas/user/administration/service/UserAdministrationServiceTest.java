package ch.sbb.atlas.user.administration.service;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import ch.sbb.atlas.user.administration.api.UserPermissionCreateModel;
import ch.sbb.atlas.user.administration.exception.UserPermissionConflictException;
import ch.sbb.atlas.user.administration.repository.UserPermissionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserAdministrationServiceTest {

    private UserAdministrationService userAdministrationService;

    @Mock
    private UserPermissionRepository userPermissionRepositoryMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userAdministrationService = new UserAdministrationService(userPermissionRepositoryMock);
    }

    @Test
    void shouldThrowUserPemissionConflictExceptionOnValidatePermissionExistence() {
        doReturn(true).when(userPermissionRepositoryMock).existsBySbbUserIdIgnoreCase(anyString());
        UserPermissionCreateModel createModel = UserPermissionCreateModel.builder()
                .sbbUserId("u123456").build();

        assertThatExceptionOfType(UserPermissionConflictException.class).isThrownBy(
                () -> userAdministrationService.validatePermissionExistence(createModel));
    }

    @Test
    void shouldNotThrowUserPemissionConflictExceptionOnValidatePermissionExistence() {
        doReturn(false).when(userPermissionRepositoryMock).existsBySbbUserIdIgnoreCase(anyString());
        UserPermissionCreateModel createModel = UserPermissionCreateModel.builder()
                .sbbUserId("u123456").build();

        Assertions.assertDoesNotThrow(() -> userAdministrationService.validatePermissionExistence(createModel));
    }

}
