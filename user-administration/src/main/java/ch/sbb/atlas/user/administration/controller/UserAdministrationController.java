package ch.sbb.atlas.user.administration.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.user.administration.PermissionModel;
import ch.sbb.atlas.api.user.administration.UserAdministrationApiV1;
import ch.sbb.atlas.api.user.administration.UserDisplayNameModel;
import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.api.user.administration.UserPermissionCreateModel;
import ch.sbb.atlas.api.user.administration.enumeration.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.service.UserService;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import ch.sbb.atlas.user.administration.exception.LimitedPageSizeRequestException;
import ch.sbb.atlas.user.administration.exception.RestrictionWithoutTypeException;
import ch.sbb.atlas.user.administration.mapper.KafkaModelMapper;
import ch.sbb.atlas.user.administration.mapper.UserPermissionMapper;
import ch.sbb.atlas.user.administration.service.ClientCredentialAdministrationService;
import ch.sbb.atlas.user.administration.service.GraphApiService;
import ch.sbb.atlas.user.administration.service.UserAdministrationService;
import ch.sbb.atlas.user.administration.service.UserPermissionDistributor;
import java.util.ArrayList;
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
  private final ClientCredentialAdministrationService clientCredentialAdministrationService;
  private final UserPermissionDistributor userPermissionDistributor;

  private final GraphApiService graphApiService;

  @Override
  public Container<UserModel> getUsers(Pageable pageable, Set<String> permissionRestrictions, PermissionRestrictionType type,
      Set<ApplicationType> applicationTypes) {
    if (pageable.getPageSize() > GraphApiService.BATCH_REQUEST_LIMIT) {
      throw new LimitedPageSizeRequestException(pageable.getPageSize(),
          GraphApiService.BATCH_REQUEST_LIMIT);
    }
    if (permissionRestrictions != null && !permissionRestrictions.isEmpty() && type == null) {
      throw new RestrictionWithoutTypeException();
    }
    Page<String> userPage = userAdministrationService.getUserPage(pageable, permissionRestrictions,
        applicationTypes, type);
    List<UserModel> userModels = graphApiService.resolveUsers(userPage.getContent());
    userModels.forEach(user -> user.setPermissions(getUserPermissionModels(user.getUserId())));
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
  public UserDisplayNameModel getUserDisplayName(String userId) {
    Optional<UserDisplayNameModel> clientCredentialAlias = getClientCredentialAlias(userId);
    if (clientCredentialAlias.isPresent()) {
      return clientCredentialAlias.get();
    }

    UserModel userModel = graphApiService.resolveUsers(List.of(userId))
        .stream()
        .findFirst().orElseThrow(() -> new IllegalStateException("User is missing"));
    return UserDisplayNameModel.toModel(userModel);
  }

  public List<UserDisplayNameModel> getUserInformation(List<String> userIds) {
    List<UserDisplayNameModel> result = new ArrayList<>();

    // Add all ClientCredential Display Informations
    userIds.forEach(userId -> getClientCredentialAlias(userId).ifPresent(result::add));

    // Add all User Information
    List<String> userIdList = userIds.stream().filter(userId -> result.stream()
        .noneMatch(i -> i.getSbbUserId().equals(userId))).toList();
    result.addAll(graphApiService.resolveUsers(userIdList).stream().map(UserDisplayNameModel::toModel).toList());

    return result;
  }

  private Optional<UserDisplayNameModel> getClientCredentialAlias(String clientId) {
    return clientCredentialAdministrationService.getClientCredentialPermission(
        clientId).stream().findFirst().map(permission -> UserDisplayNameModel.builder()
        .sbbUserId(clientId)
        .displayName(permission.getAlias())
        .build());
  }

  @Override
  public UserModel getCurrentUser() {
    return getUser(UserService.getUserIdentifier());
  }

  @Override
  public UserModel createUserPermission(UserPermissionCreateModel userPermissionCreate) {
    userAdministrationService.save(userPermissionCreate);
    UserModel userModel = getUser(userPermissionCreate.getSbbUserId());
    userPermissionDistributor.pushUserPermissionToKafka(KafkaModelMapper.toKafkaModel(userModel));
    return userModel;
  }

  private Set<PermissionModel> getUserPermissionModels(String userId) {
    return getUserPermissionModels(userAdministrationService.getUserPermissions(userId));
  }

  private Set<PermissionModel> getUserPermissionModels(List<UserPermission> userPermissions) {
    return userPermissions.stream().map(UserPermissionMapper::toModel).collect(Collectors.toSet());
  }

  @Override
  public UserModel updateUserPermissions(UserPermissionCreateModel editedPermissions) {
    userAdministrationService.updateUser(editedPermissions);
    UserModel userModel = getUser(editedPermissions.getSbbUserId());
    userPermissionDistributor.pushUserPermissionToKafka(KafkaModelMapper.toKafkaModel(userModel));
    return userModel;
  }

}
