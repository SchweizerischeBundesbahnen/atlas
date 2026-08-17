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
  private UserManualMailOverrideRepository userManualMailOverrideRepository;

  @Test
  void shouldPersistManualMailForUser() {
    // Given
    UserManualMailOverride manualMail = UserManualMailOverride.builder()
        .sbbUserId("u123456")
        .mail("manual@sbb.ch")
        .build();

    // When
    userManualMailOverrideRepository.saveAndFlush(manualMail);

    // Then
    Optional<UserManualMailOverride> persisted = userManualMailOverrideRepository.findBySbbUserIdIgnoreCase("u123456");
    assertThat(persisted).isPresent();
    assertThat(persisted.get().getMail()).isEqualTo("manual@sbb.ch");
  }

  @Test
  void shouldRejectSecondManualMailForSameUser() {
    // Given
    userManualMailOverrideRepository.saveAndFlush(
        UserManualMailOverride.builder().sbbUserId("u123456").mail("first@sbb.ch").build());

    // When / Then
    UserManualMailOverride userManualMailOverride = UserManualMailOverride.builder().sbbUserId("u123456").mail("second@sbb.ch")
        .build();

    assertThatThrownBy(() -> userManualMailOverrideRepository.saveAndFlush(userManualMailOverride))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  void shouldAllowSameManualMailForTwoDifferentUsers() {
    // Given
    userManualMailOverrideRepository.saveAndFlush(
        UserManualMailOverride.builder().sbbUserId("u123456").mail("shared@sbb.ch").build());

    // When
    userManualMailOverrideRepository.saveAndFlush(
        UserManualMailOverride.builder().sbbUserId("u999999").mail("shared@sbb.ch").build());

    // Then
    assertThat(userManualMailOverrideRepository.findAll()).hasSize(2);
  }

  @Test
  void shouldFindManualMailByMailIgnoringCase() {
    // Given
    userManualMailOverrideRepository.saveAndFlush(
        UserManualMailOverride.builder().sbbUserId("u123456").mail("Manual@Sbb.ch").build());

    // When
    Optional<UserManualMailOverride> found = userManualMailOverrideRepository.findByMailIgnoreCase("manual@sbb.ch");

    // Then
    assertThat(found).isPresent();
    assertThat(found.get().getSbbUserId()).isEqualTo("u123456");
  }

  @Test
  void shouldFindManualMailsByPartialMailIgnoringCase() {
    // Given
    userManualMailOverrideRepository.saveAndFlush(
        UserManualMailOverride.builder().sbbUserId("u123456").mail("Manual.Contact@Sbb.ch").build());
    userManualMailOverrideRepository.saveAndFlush(
        UserManualMailOverride.builder().sbbUserId("u999999").mail("other@sbb.ch").build());

    // When
    List<UserManualMailOverride> found = userManualMailOverrideRepository.findTop10ByMailContainingIgnoreCase("manual.contact");

    // Then
    assertThat(found).extracting(UserManualMailOverride::getSbbUserId).containsExactly("u123456");
  }

  @Test
  void shouldFindManualMailsForMultipleUserIdsInOneQuery() {
    // Given
    userManualMailOverrideRepository.saveAndFlush(
        UserManualMailOverride.builder().sbbUserId("u111111").mail("one@sbb.ch").build());
    userManualMailOverrideRepository.saveAndFlush(
        UserManualMailOverride.builder().sbbUserId("u222222").mail("two@sbb.ch").build());
    userManualMailOverrideRepository.saveAndFlush(
        UserManualMailOverride.builder().sbbUserId("u333333").mail("three@sbb.ch").build());

    // When
    List<UserManualMailOverride> found = userManualMailOverrideRepository.findAllBySbbUserIdInIgnoreCase(
        List.of("U111111", "u222222", "u444444"));

    // Then
    assertThat(found).extracting(UserManualMailOverride::getSbbUserId).containsExactlyInAnyOrder("u111111", "u222222");
  }

}
