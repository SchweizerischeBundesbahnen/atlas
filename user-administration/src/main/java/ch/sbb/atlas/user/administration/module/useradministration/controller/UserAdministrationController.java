package ch.sbb.atlas.user.administration.module.useradministration.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.user.administration.PermissionModel;
import ch.sbb.atlas.api.user.administration.UserAdministrationApiV1;
import ch.sbb.atlas.api.user.administration.UserDisplayNameModel;
import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.api.user.administration.UserPermissionCreateModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.model.exception.SimpleAtlasException;
import ch.sbb.atlas.service.UserService;
import ch.sbb.atlas.user.administration.mapper.KafkaModelMapper;
import ch.sbb.atlas.user.administration.module.clientcredential.mapper.ClientCredentialMapper;
import ch.sbb.atlas.user.administration.module.clientcredential.service.ClientCredentialAdministrationService;
import ch.sbb.atlas.user.administration.module.useradministration.entity.UserPermission;
import ch.sbb.atlas.user.administration.module.useradministration.exception.RestrictionWithoutTypeException;
import ch.sbb.atlas.user.administration.module.useradministration.mapper.UserPermissionMapper;
import ch.sbb.atlas.user.administration.module.useradministration.service.UserAdministrationService;
import ch.sbb.atlas.user.administration.module.useradministration.service.UserPermissionDistributor;
import ch.sbb.atlas.user.administration.module.userinformation.service.GraphApiService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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
    UserModel user = userModel.orElseThrow(() -> displayUserNotFoundException(userId));
    user.setPermissions(getUserPermissionModels(userId));
    return user;
  }

  @Override
  public UserModel getUserByMail(String mail) {
    Optional<UserModel> userModel = graphApiService.searchUserByMail(mail)
        .stream()
        .findFirst();

    UserModel user = userModel.orElseThrow(() -> displayUserNotFoundException(mail));

    user.setPermissions(getUserPermissionModels(user.getUserId()));
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
        .findFirst().orElseThrow(() -> displayUserNotFoundException(userId));
    return UserDisplayNameModel.toModel(userModel);
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
  public UserModel updateUserPermissions(String userId, ApplicationType application, PermissionModel editedPermissions) {
    userAdministrationService.updatePermission(userId, application, editedPermissions);
    UserModel userModel = getUser(userId);
    userPermissionDistributor.pushUserPermissionToKafka(KafkaModelMapper.toKafkaModel(userModel));
    return userModel;
  }

  @Override
  public void syncPermissions() {
    log.info("Start user/client-permissions sync...");
    syncClientCredentials();
    syncUserCredentials();
    log.info("Sync completed. Goodbye.");
  }

  private void syncUserCredentials() {
    log.info("Starting to sync each user permission to kafka topic");
    List<UserPermission> allUser = userAdministrationService.getAllUsers();
    allUser.forEach(userPermission -> {
      UserModel userModel = UserModel.builder()
          .permissions(Set.of(UserPermissionMapper.toModel(userPermission)))
          .sbbUserId(userPermission.getSbbUserId())
          .build();
      UserAdministrationModel kafkaModel = KafkaModelMapper.toKafkaModel(userModel);
      userPermissionDistributor.pushUserPermissionToKafka(kafkaModel);
    });
    log.info("Users were synched to kafka");
  }

  private void syncClientCredentials() {
    log.info("Starting to sync each client permission to kafka topic");
    ClientCredentialMapper.toModel(clientCredentialAdministrationService.getClientCredentialPermissions())
        .forEach(clientCredentialPermission -> userPermissionDistributor.pushUserPermissionToKafka(
            KafkaModelMapper.toKafkaModel(clientCredentialPermission)));
    log.info("ClientCredentials were synched to kafka");
  }

  private SimpleAtlasException displayUserNotFoundException(String user) {
    return SimpleAtlasException.builder()
        .message("User not found: " + user)
        .status(HttpStatus.NOT_FOUND)
        .displayCode("USER_ADMIN.NOT_FOUND")
        .error("User not Found")
        .build();
  }
}
