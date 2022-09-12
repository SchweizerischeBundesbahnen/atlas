package ch.sbb.atlas.user.administration.controller;

import ch.sbb.atlas.base.service.model.api.Container;
import ch.sbb.atlas.base.service.model.service.UserService;
import ch.sbb.atlas.user.administration.api.UserAdministrationApiV1;
import ch.sbb.atlas.user.administration.api.UserPermissionCreateModel;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import ch.sbb.atlas.user.administration.exception.LimitedPageSizeRequestException;
import ch.sbb.atlas.user.administration.models.UserModel;
import ch.sbb.atlas.user.administration.models.UserPermissionModel;
import ch.sbb.atlas.user.administration.service.GraphApiService;
import ch.sbb.atlas.user.administration.service.UserAdministrationService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserAdministrationController implements UserAdministrationApiV1 {

  private final UserAdministrationService userAdministrationService;

  private final GraphApiService graphApiService;

  @Override
  public Container<UserModel> getUsers(Pageable pageable) {
    if (pageable.getPageSize() > GraphApiService.BATCH_REQUEST_LIMIT) {
      throw new LimitedPageSizeRequestException(pageable.getPageSize(),
          GraphApiService.BATCH_REQUEST_LIMIT);
    }
    Page<String> userPage = userAdministrationService.getUserPage(pageable);
    List<UserModel> userModels = graphApiService.resolveUsers(userPage.getContent());
    userModels.forEach(
        user -> user.setPermissions(getUserPermissionModels(user.getSbbUserId())));
    return Container.<UserModel>builder()
                    .totalCount(userPage.getTotalElements())
                    .objects(userModels)
                    .build();
  }

  @Override
  public UserModel getUser(String userId) {
    Optional<UserModel> userModel = graphApiService.resolveUsers(List.of(userId))
                                                   .stream()
                                                   .findFirst();
    UserModel user = userModel.orElseThrow(() -> new IllegalStateException("User is missing"));
    user.setPermissions(getUserPermissionModels(userId));
    return user;
  }

  @Override
  public UserModel getCurrentUser() {
    return getUser(UserService.getSbbUid());
  }

  private Set<UserPermissionModel> getUserPermissionModels(String userId) {
    return userAdministrationService.getUserPermissions(userId).stream()
                                    .map(
                                        UserPermissionModel::toModel)
                                    .collect(
                                        Collectors.toSet());
  }
  @Override
  public UserModel createUserPermission(UserPermissionCreateModel user) {
    // validate
    userAdministrationService.validateUserPermissionCreation(user);
    // create
    final List<UserPermission> toSave = new ArrayList<>();
    for (UserPermissionModel permission : user.getPermissions()) {
      UserPermission userPermission = UserPermission.builder()
                                                    .sbbUserId(user.getSbbUserId().toLowerCase())
                                                    .application(permission.getApplication())
                                                    .role(permission.getRole())
                                                    .sboid(new HashSet<>(permission.getSboids())).build();
      toSave.add(userPermission);
    }
    List<UserPermission> savedUserPermissions = userAdministrationService.save(toSave);
    // return created UserModel
    final List<UserModel> userModels = graphApiService.resolveUsers(List.of(user.getSbbUserId()));
    userModels.get(0).setPermissions(getUserPermissionModels(savedUserPermissions));
    return userModels.get(0);
  }

  private Set<UserPermissionModel> getUserPermissionModels(String userId) {
    return getUserPermissionModels(userAdministrationService.getUserPermissions(userId));
  }

  private Set<UserPermissionModel> getUserPermissionModels(List<UserPermission> userPermissions) {
    return userPermissions.stream().map(UserPermissionModel::toModel).collect(Collectors.toSet());
  }

}
