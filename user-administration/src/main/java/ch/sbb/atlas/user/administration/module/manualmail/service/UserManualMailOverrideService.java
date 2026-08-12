package ch.sbb.atlas.user.administration.module.manualmail.service;

import ch.sbb.atlas.user.administration.module.manualmail.entity.UserManualMailOverride;
import ch.sbb.atlas.user.administration.module.manualmail.repository.UserManualMailOverrideRepository;
import java.util.Collection;
import java.util.List;
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
public class UserManualMailOverrideService {

  private final UserManualMailOverrideRepository userManualMailOverrideRepository;

  public void setManualMailOverride(String sbbUserId, String mail) {
    UserManualMailOverride manualMail = userManualMailOverrideRepository.findBySbbUserIdIgnoreCase(sbbUserId)
        .map(existing -> {
          existing.setMail(mail);
          return existing;
        })
        .orElseGet(() -> UserManualMailOverride.builder().sbbUserId(sbbUserId).mail(mail).build());
    userManualMailOverrideRepository.save(manualMail);
  }

  public void deleteManualMailOverride(String sbbUserId) {
    userManualMailOverrideRepository.deleteBySbbUserIdIgnoreCase(sbbUserId);
  }

  public Map<String, String> getMailsByUserIds(Collection<String> sbbUserIds) {
    if (sbbUserIds == null || sbbUserIds.isEmpty()) {
      return Map.of();
    }
    return userManualMailOverrideRepository.findAllBySbbUserIdInIgnoreCase(sbbUserIds).stream()
        .collect(Collectors.toMap(UserManualMailOverride::getSbbUserId, UserManualMailOverride::getMail));
  }

  public Optional<String> findUserIdByMail(String mail) {
    return userManualMailOverrideRepository.findByMailIgnoreCase(mail).map(UserManualMailOverride::getSbbUserId);
  }

  /**
   * Finds the users whose manually overridden mail matches the given (partial) search query. Used to make users searchable by
   * their overridden mail, which is unknown to Azure/Graph.
   */
  public List<String> findUserIdsByMailContaining(String searchQuery) {
    if (StringUtils.isBlank(searchQuery)) {
      return List.of();
    }
    return userManualMailOverrideRepository.findTop10ByMailContainingIgnoreCase(searchQuery.trim()).stream()
        .map(UserManualMailOverride::getSbbUserId)
        .toList();
  }

}
