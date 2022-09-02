package ch.sbb.atlas.user.administration.service;

import ch.sbb.atlas.user.administration.entity.UserPermission;
import ch.sbb.atlas.user.administration.repository.UserPermissionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAdministrationService {

  private final UserPermissionRepository userPermissionRepository;

  public Page<String> getUserPage(Pageable pageable) {
    return userPermissionRepository.findAllDistinctSbbUserId(pageable);
  }

  public List<UserPermission> getUserPermissions(String sbbUserId){
    return userPermissionRepository.findBySbbUserIdEqualsIgnoreCase(sbbUserId);
  }

}
