package ch.sbb.atlas.user.administration.security.service;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionRestrictionModel;
import ch.sbb.atlas.user.administration.security.UserPermissionHolder;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BulkImportUserAdministrationService extends BusinessOrganisationBasedUserAdministrationService {

  public BulkImportUserAdministrationService(UserPermissionHolder userPermissionHolder) {
    super(userPermissionHolder);
  }

  public boolean hasPermissionsForBulkImport(ApplicationType applicationType) {
    UserAdministrationPermissionModel userPermissionsForApplication = getUserPermissionsForApplication(
        applicationType);

    boolean hasExplicitBulkImportPermission = false;
    Optional<UserAdministrationPermissionRestrictionModel> bulkImportPermission = userPermissionsForApplication.getRestrictions()
        .stream()
        .filter(i -> i.getRestrictionType() == PermissionRestrictionType.BULK_IMPORT)
        .findFirst();
    if (bulkImportPermission.isPresent()) {
      hasExplicitBulkImportPermission = Boolean.parseBoolean(bulkImportPermission.get().getValue());
    }

    boolean hasPermissionsForBulkImport = hasExplicitBulkImportPermission || isAtLeastSupervisor(applicationType);
    log.info("User {} hasPermissionsForBulkImport={}", getCurrentUserSbbUid(), hasPermissionsForBulkImport);
    return hasPermissionsForBulkImport;
  }

}
