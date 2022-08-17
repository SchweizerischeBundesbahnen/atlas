package ch.sbb.line.directory.service;

import ch.sbb.line.directory.entity.UserPermission;
import ch.sbb.line.directory.repository.UserPermissionRepository;
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
