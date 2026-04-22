package ch.sbb.atlas.user.administration.security.service;

import ch.sbb.atlas.api.model.BoContactAssociated;
import ch.sbb.atlas.service.UserService;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BoUserMailCheckService {

  public boolean isCurrentUserAssignedTo(BoContactAssociated boContactAssociated) {
    boolean isMailAssigned = isCurrentUserMailAssignedTo(boContactAssociated.getBoContactMail());
    boolean isSbbuidAssigned = isCurrentUserSbbUidAssignedTo(boContactAssociated.getBoContactSbbuid());

    return isMailAssigned || isSbbuidAssigned;
  }

  public boolean isCurrentUserMailAssignedTo(String boMail) {
    if (boMail == null) {
      log.info("boMail not given for assignedTo check");
      return false;
    }
    String preferredUsername = UserService.getPreferredUsername();
    log.info("{} is equal to boMail: {}", preferredUsername, boMail);
    return Objects.equals(boMail, preferredUsername);
  }

  public boolean isCurrentUserSbbUidAssignedTo(String boSbbuid) {
    if (boSbbuid == null) {
      log.info("boSbbuid not given for assignedTo check");
      return false;
    }
    String userIdentifier = UserService.getUserIdentifier();
    log.info("{} is equal to boSbbuid: {}", userIdentifier, boSbbuid);
    return Objects.equals(userIdentifier, boSbbuid);
  }

}
