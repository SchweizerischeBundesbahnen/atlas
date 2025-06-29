package ch.sbb.prm.directory.security;

import ch.sbb.atlas.kafka.model.service.point.SharedServicePointVersionModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.security.UserPermissionHolder;
import ch.sbb.atlas.user.administration.security.service.BusinessOrganisationBasedUserAdministrationService;
import ch.sbb.prm.directory.entity.PrmSharedVersion;
import ch.sbb.prm.directory.service.SharedServicePointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PrmUserAdministrationService extends BusinessOrganisationBasedUserAdministrationService {

    private final SharedServicePointService sharedServicePointService;

    public PrmUserAdministrationService(UserPermissionHolder userPermissionHolder,
                                        SharedServicePointService sharedServicePointService) {
        super(userPermissionHolder);
        this.sharedServicePointService = sharedServicePointService;
    }

    public boolean hasUserRightsToCreateOrEditPrmObject(PrmSharedVersion version) {

        SharedServicePointVersionModel sharedServicePointVersionModel = sharedServicePointService
                .validateServicePointExists(version.getParentServicePointSloid());

        if (sharedServicePointVersionModel == null || sharedServicePointVersionModel.getSboids().isEmpty()) {
            log.error("List of provided SBOIDs was empty. Cannot perform check permissions. Will deny operation");
            return false;
        }
        log.info("Checking if user has appropriate BusinessOrganisation permissions to perform edit or create of PRM object");
        return sharedServicePointVersionModel.getSboids().stream()
                .anyMatch(sboid -> hasUserPermissionsForBusinessOrganisation(sboid, ApplicationType.PRM));
    }

    public boolean isAtLeastPrmSupervisor(){
        return isAtLeastSupervisor(ApplicationType.PRM);
    }

}
