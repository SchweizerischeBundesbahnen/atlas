package ch.sbb.atlas.user.administration.service;

import ch.sbb.atlas.user.administration.api.UserPermissionCreateModel;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import ch.sbb.atlas.user.administration.exception.UserPermissionConflictException;
import ch.sbb.atlas.user.administration.enumeration.ApplicationRole;
import ch.sbb.atlas.user.administration.enumeration.ApplicationType;
import ch.sbb.atlas.user.administration.enumeration.UserAccountStatus;
import ch.sbb.atlas.user.administration.models.UserModel;
import ch.sbb.atlas.user.administration.models.UserPermissionModel;
import ch.sbb.atlas.user.administration.repository.UserPermissionRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAdministrationService {

  private final UserPermissionRepository userPermissionRepository;

  public Page<String> getUserPage(Pageable pageable) {
    return userPermissionRepository.findAllDistinctSbbUserId(pageable);
  }

  public List<UserPermission> getUserPermissions(String sbbUserId) {
    return userPermissionRepository.findBySbbUserId(sbbUserId);
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
      if (editedPermission.getRole() == ApplicationRole.READER) {
        userPermissionRepository.delete(userPermission);
      } else {
        userPermission.setRole(editedPermission.getRole());
        userPermission.setSboid(new HashSet<>(editedPermission.getSboids()));
      }
    };
  }

  Optional<UserPermission> getCurrentUserPermission(String sbbuid, ApplicationType applicationType) {
    return userPermissionRepository.findBySbbUserId(sbbuid)
                                   .stream()
                                   .filter(userPermission -> userPermission.getApplication()
                                       == applicationType)
                                   .findFirst();
  }
}
