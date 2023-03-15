package ch.sbb.atlas.user.administration.service;

import ch.sbb.atlas.api.user.administration.UserPermissionCreateModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import ch.sbb.atlas.user.administration.exception.UserPermissionConflictException;
import ch.sbb.atlas.user.administration.mapper.UserPermissionCreateMapper;
import ch.sbb.atlas.user.administration.mapper.UserPermissionMapper;
import ch.sbb.atlas.user.administration.repository.UserPermissionRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

  public Page<String> getUserPage(Pageable pageable, Set<String> sboids, Set<ApplicationType> applicationTypes) {
    sboids = Optional.ofNullable(sboids).orElse(new HashSet<>());
    applicationTypes = Optional.ofNullable(applicationTypes).orElse(new HashSet<>());
    return userPermissionRepository.getFilteredUsers(pageable, applicationTypes, sboids);
  }

  public List<UserPermission> getUserPermissions(String sbbUserId) {
    return userPermissionRepository.findBySbbUserIdIgnoreCase(sbbUserId);
  }

  private void validatePermissionExistence(String sbbUserId) {
    boolean exists = userPermissionRepository.existsBySbbUserIdIgnoreCase(sbbUserId);
    if (exists) {
      throw new UserPermissionConflictException(sbbUserId);
    }
  }

  public void save(UserPermissionCreateModel userPermissionCreate) {
    validatePermissionExistence(userPermissionCreate.getSbbUserId());
    final List<UserPermission> toSave = UserPermissionCreateMapper.toEntityList(userPermissionCreate);
    userPermissionRepository.saveAll(toSave);
  }

  public void updateUser(UserPermissionCreateModel editedPermissions) {
    editedPermissions.getPermissions().forEach(editedPermission -> {
      Optional<UserPermission> existingPermissions = getCurrentUserPermission(editedPermissions.getSbbUserId(),
          editedPermission.getApplication());

      if (existingPermissions.isPresent()) {
        UserPermission updateableUserPermission = existingPermissions.get();
        updateableUserPermission.setRole(editedPermission.getRole());
        updateableUserPermission.setSboid(new HashSet<>(editedPermission.getSboids()));
        updateableUserPermission.setSwissCantons(new HashSet<>(editedPermission.getSwissCantons()));
      } else {
        UserPermission additionalUserPermission = UserPermissionMapper.toEntity(editedPermissions.getSbbUserId(),
            editedPermission);
        userPermissionRepository.save(additionalUserPermission);
      }
    });
  }

  Optional<UserPermission> getCurrentUserPermission(String sbbuid, ApplicationType applicationType) {
    return userPermissionRepository.findBySbbUserIdIgnoreCase(sbbuid)
        .stream()
        .filter(userPermission -> userPermission.getApplication()
            == applicationType)
        .findFirst();
  }
}
