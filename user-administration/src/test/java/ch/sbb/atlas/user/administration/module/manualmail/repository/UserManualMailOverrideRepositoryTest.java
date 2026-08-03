package ch.sbb.atlas.user.administration.module.manualmail.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.user.administration.module.manualmail.entity.UserManualMailOverride;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class UserManualMailOverrideRepositoryTest {

  @Autowired
  private UserManualMailRepository userManualMailRepository;

  @Test
  void shouldPersistManualMailForUser() {
    // Given
    UserManualMailOverride manualMail = UserManualMailOverride.builder()
        .sbbUserId("u123456")
        .mail("manual@sbb.ch")
        .build();

    // When
    userManualMailRepository.saveAndFlush(manualMail);

    // Then
    Optional<UserManualMailOverride> persisted = userManualMailRepository.findBySbbUserIdIgnoreCase("u123456");
    assertThat(persisted).isPresent();
    assertThat(persisted.get().getMail()).isEqualTo("manual@sbb.ch");
  }

  @Test
  void shouldRejectSecondManualMailForSameUser() {
    // Given
    userManualMailRepository.saveAndFlush(
        UserManualMailOverride.builder().sbbUserId("u123456").mail("first@sbb.ch").build());

    // When / Then
    UserManualMailOverride userManualMailOverride = UserManualMailOverride.builder().sbbUserId("u123456").mail("second@sbb.ch")
        .build();

    assertThatThrownBy(() -> userManualMailRepository.saveAndFlush(userManualMailOverride))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  void shouldAllowSameManualMailForTwoDifferentUsers() {
    // Given
    userManualMailRepository.saveAndFlush(
        UserManualMailOverride.builder().sbbUserId("u123456").mail("shared@sbb.ch").build());

    // When
    userManualMailRepository.saveAndFlush(
        UserManualMailOverride.builder().sbbUserId("u999999").mail("shared@sbb.ch").build());

    // Then
    assertThat(userManualMailRepository.findAll()).hasSize(2);
  }

  @Test
  void shouldFindManualMailByMailIgnoringCase() {
    // Given
    userManualMailRepository.saveAndFlush(
        UserManualMailOverride.builder().sbbUserId("u123456").mail("Manual@Sbb.ch").build());

    // When
    Optional<UserManualMailOverride> found = userManualMailRepository.findByMailIgnoreCase("manual@sbb.ch");

    // Then
    assertThat(found).isPresent();
    assertThat(found.get().getSbbUserId()).isEqualTo("u123456");
  }

  @Test
  void shouldFindManualMailsForMultipleUserIdsInOneQuery() {
    // Given
    userManualMailRepository.saveAndFlush(
        UserManualMailOverride.builder().sbbUserId("u111111").mail("one@sbb.ch").build());
    userManualMailRepository.saveAndFlush(
        UserManualMailOverride.builder().sbbUserId("u222222").mail("two@sbb.ch").build());
    userManualMailRepository.saveAndFlush(
        UserManualMailOverride.builder().sbbUserId("u333333").mail("three@sbb.ch").build());

    // When
    List<UserManualMailOverride> found = userManualMailRepository.findAllBySbbUserIdInIgnoreCase(
        List.of("U111111", "u222222", "u444444"));

    // Then
    assertThat(found).extracting(UserManualMailOverride::getSbbUserId).containsExactlyInAnyOrder("u111111", "u222222");
  }

}
