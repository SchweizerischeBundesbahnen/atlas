package ch.sbb.atlas.user.administration.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserAdministrationService {

  public boolean hasUserPermissions(){

    return false;
  }

  public boolean hasUserPermissionsToCreate(String sbbuid, BusinessOrganisationAssociated businessObject){

    return false;
  }

  private ApplicationRole getRole(){
    return ApplicationRole.ADMIN;
  }
  
}
