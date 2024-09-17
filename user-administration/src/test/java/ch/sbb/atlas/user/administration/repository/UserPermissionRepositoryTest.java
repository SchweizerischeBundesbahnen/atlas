package ch.sbb.atlas.user.administration.repository;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
@Transactional
class UserPermissionRepositoryTest {

    private final UserPermissionRepository userPermissionRepository;


    @Autowired
    UserPermissionRepositoryTest(UserPermissionRepository userPermissionRepository) {
        this.userPermissionRepository = userPermissionRepository;
    }

    @BeforeEach
    void setUp() {
        UserPermission userPermissionInSepodi = UserPermission.builder()
                .sbbUserId("u123456")
                .role(ApplicationRole.SUPERVISOR)
                .application(ApplicationType.SEPODI)
                .build();

        UserPermission userPermissionInPRM = UserPermission.builder()
                .sbbUserId("u239096")
                .role(ApplicationRole.SUPERVISOR)
                .application(ApplicationType.PRM)
                .build();

        userPermissionRepository.saveAndFlush(userPermissionInSepodi);
        userPermissionRepository.saveAndFlush(userPermissionInPRM);
    }

    @Test
    void findBySbbUserIdIgnoreCase() {

    }

    @Test
    void findBySbbUserIdIgnoreCaseAndApplication() {
        Optional<UserPermission> userPermission = userPermissionRepository.findBySbbUserIdIgnoreCaseAndApplication("u123456", ApplicationType.SEPODI);
        assertThat(userPermission.isPresent()).isTrue();
    }
}