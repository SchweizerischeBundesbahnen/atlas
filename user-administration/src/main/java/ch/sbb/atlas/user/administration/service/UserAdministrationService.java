package ch.sbb.atlas.user.administration.service;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.api.UserPermissionCreateModel;
import ch.sbb.atlas.user.administration.api.UserPermissionModel;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import ch.sbb.atlas.user.administration.exception.UserPermissionConflictException;
import ch.sbb.atlas.user.administration.repository.UserPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAdministrationService {

  private final UserPermissionRepository userPermissionRepository;

  public Page<String> getUserPage(Pageable pageable) {
    return userPermissionRepository.findAllDistinctSbbUserId(pageable);
  }

  public List<UserPermission> getUserPermissions(String sbbUserId) {
    return userPermissionRepository.findBySbbUserIdIgnoreCase(sbbUserId);
  }

  public void validatePermissionExistence(UserPermissionCreateModel user) {
    boolean exists = userPermissionRepository.existsBySbbUserIdIgnoreCase(user.getSbbUserId());
    if (exists) {
      throw new UserPermissionConflictException(user.getSbbUserId());
    }
  }

  public List<UserPermission> save(List<UserPermission> userPermissions) {
    return userPermissionRepository.saveAll(userPermissions);
  }

  public void updateUser(UserPermissionCreateModel editedPermissions) {
    editedPermissions.getPermissions().forEach(editedPermission -> {
      Optional<UserPermission> existingPermissions = getCurrentUserPermission(
          editedPermissions.getSbbUserId(),
          editedPermission.getApplication());
      existingPermissions.ifPresent(updateExistingPermissions(editedPermission));
    });
  }

  private Consumer<UserPermission> updateExistingPermissions(UserPermissionModel editedPermission) {
    return userPermission -> {
      userPermission.setRole(editedPermission.getRole());
      userPermission.setSboid(new HashSet<>(editedPermission.getSboids()));
    };
  }

  Optional<UserPermission> getCurrentUserPermission(String sbbuid, ApplicationType applicationType) {
    return userPermissionRepository.findBySbbUserIdIgnoreCase(sbbuid)
                                   .stream()
                                   .filter(userPermission -> userPermission.getApplication()
                                       == applicationType)
                                   .findFirst();
  }
}
