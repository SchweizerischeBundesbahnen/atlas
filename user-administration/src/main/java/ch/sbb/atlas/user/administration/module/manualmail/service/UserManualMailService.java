package ch.sbb.atlas.user.administration.module.manualmail.service;

import ch.sbb.atlas.user.administration.module.manualmail.entity.UserManualMail;
import ch.sbb.atlas.user.administration.module.manualmail.repository.UserManualMailRepository;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserManualMailService {

  private final UserManualMailRepository userManualMailRepository;

  public void upsert(String sbbUserId, String mail) {
    if (StringUtils.isBlank(mail)) {
      delete(sbbUserId);
      return;
    }
    UserManualMail manualMail = userManualMailRepository.findBySbbUserIdIgnoreCase(sbbUserId)
        .map(existing -> {
          existing.setMail(mail);
          return existing;
        })
        .orElseGet(() -> UserManualMail.builder().sbbUserId(sbbUserId).mail(mail).build());
    userManualMailRepository.save(manualMail);
  }

  public void delete(String sbbUserId) {
    userManualMailRepository.deleteBySbbUserIdIgnoreCase(sbbUserId);
  }

  public Map<String, String> getMailsByUserIds(Collection<String> sbbUserIds) {
    if (sbbUserIds == null || sbbUserIds.isEmpty()) {
      return Map.of();
    }
    return userManualMailRepository.findAllBySbbUserIdInIgnoreCase(sbbUserIds).stream()
        .collect(Collectors.toMap(UserManualMail::getSbbUserId, UserManualMail::getMail));
  }

  public Optional<String> findUserIdByMail(String mail) {
    return userManualMailRepository.findByMailIgnoreCase(mail).map(UserManualMail::getSbbUserId);
  }

}
