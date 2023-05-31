package ch.sbb.atlas.user.administration.security.service;

import ch.sbb.atlas.api.model.BusinessOrganisationAssociated;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import ch.sbb.atlas.user.administration.security.UserPermissionHolder;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BusinessOrganisationBasedUserAdministrationService extends BaseUserAdministrationService {

  public BusinessOrganisationBasedUserAdministrationService(UserPermissionHolder userPermissionHolder) {
    super(userPermissionHolder);
  }

  public boolean hasUserPermissionsToCreate(BusinessOrganisationAssociated businessObject,
      ApplicationType applicationType) {
    log.info("Checking if user {} may create object with sboid {}",
        getCurrentUserSbbUid(),
        businessObject.getBusinessOrganisation());
    boolean permissionsToCreate = hasUserPermissions(applicationType,
        permissions -> permissions.getSboids()
            .contains(businessObject.getBusinessOrganisation()));
    log.info("User {} has permissions: {}", getCurrentUserSbbUid(),
        permissionsToCreate);
    return permissionsToCreate;

  }

  public boolean hasUserPermissionsToUpdate(BusinessOrganisationAssociated editedBusinessObject,
      List<BusinessOrganisationAssociated> currentBusinessObjects,
      ApplicationType applicationType) {
    log.info("Checking if user {} may update object {}",
        getCurrentUserSbbUid(),
        editedBusinessObject);
    boolean permissionsToUpdate = hasUserPermissions(applicationType,
        permissions -> permissions.getSboids()
            .containsAll(UpdateAffectedVersionLocator.findUpdateAffectedCurrentVersions(
                    editedBusinessObject,
                    currentBusinessObjects).stream()
                .map(
                    BusinessOrganisationAssociated::getBusinessOrganisation)
                .toList()));
    log.info("User {} has permissions: {}", getCurrentUserSbbUid(),
        permissionsToUpdate);
    return permissionsToUpdate;
  }

  public boolean isAtLeastSupervisor(ApplicationType applicationType) {
    log.info("Checking if user {} is at least supervisor for {}",
        getCurrentUserSbbUid(),
        applicationType);
    if (isAdmin()) {
      return true;
    }
    UserAdministrationPermissionModel userPermissionsForApplication = getUserPermissionsForApplication(
        applicationType);

    ApplicationRole roleForApplication = userPermissionsForApplication.getRole();
    return ApplicationRole.SUPERVISOR == roleForApplication;
  }

}
