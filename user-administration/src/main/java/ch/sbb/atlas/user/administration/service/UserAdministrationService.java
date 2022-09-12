package ch.sbb.atlas.user.administration.service;

import ch.sbb.atlas.user.administration.api.UserPermissionCreateModel;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import ch.sbb.atlas.user.administration.enumeration.ApplicationRole;
import ch.sbb.atlas.user.administration.enumeration.ApplicationType;
import ch.sbb.atlas.user.administration.enumeration.UserAccountStatus;
import ch.sbb.atlas.user.administration.models.UserModel;
import ch.sbb.atlas.user.administration.repository.UserPermissionRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAdministrationService {

  private final UserPermissionRepository userPermissionRepository;

  private final GraphApiService graphApiService;

  public Page<String> getUserPage(Pageable pageable) {
    return userPermissionRepository.findAllDistinctSbbUserId(pageable);
  }

  public List<UserPermission> getUserPermissions(String sbbUserId) {
    return userPermissionRepository.findBySbbUserId(sbbUserId);
  }

  public void validateUserPermissionCreation(UserPermissionCreateModel user) {
    boolean exists = userPermissionRepository.existsBySbbUserIdIgnoreCase(user.getSbbUserId());
    if (exists) {
      throw new ValidationException("User already exists");
    }

    List<UserModel> resolvedUsers = graphApiService.resolveUsers(List.of(user.getSbbUserId()));
    if (resolvedUsers.get(0).getAccountStatus() == UserAccountStatus.DELETED) {
      throw new ValidationException("User does not exist");
    }

    // application unique
    Set<ApplicationType> applicationTypesInPermissions = new HashSet<>();
    user.getPermissions()
        .forEach(permission -> applicationTypesInPermissions.add(permission.getApplication()));
    if (applicationTypesInPermissions.size() != user.getPermissions().size()) {
      throw new ValidationException("Duplicate application types");
    }

    // only sboids when writer role
    user.getPermissions().forEach(permission -> {
      if (permission.getRole() != ApplicationRole.WRITER && permission.getSboids().size() > 0) {
        throw new ValidationException("No sboids permitted when not writer role");
      }
    });

    user.getPermissions()
        .forEach(permission -> {
          Set<String> sboidsInPermission = new HashSet<>(permission.getSboids());
          if (sboidsInPermission.size() != permission.getSboids().size()) {
            throw new ValidationException("Duplicate sboids in permission");
          }
        });

    // TODO: maybe sboids exists
  }

  public List<UserPermission> save(List<UserPermission> userPermissions) {
    return userPermissionRepository.saveAll(userPermissions);
  }

}
