package ch.sbb.atlas.model.mail;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class MailNotificationTest {

  @Test
  void shouldReturnToAsArray() {
    // Given
    MailNotification mailNotification = MailNotification.builder().to(List.of("antonio@nelle.vacanze")).build();

    // When
    String[] toAsArray = mailNotification.toAsArray();

    // Then
    assertThat(toAsArray).isNotEmpty().containsExactly("antonio@nelle.vacanze");
  }

  @Test
  void shouldReturnCcAsArray() {
    // Given
    MailNotification mailNotification = MailNotification.builder().cc(List.of("antonio@nelle.vacanze")).build();

    // When
    String[] ccAsArray = mailNotification.ccAsArray();

    // Then
    assertThat(ccAsArray).isNotEmpty().containsExactly("antonio@nelle.vacanze");
  }

  @Test
  void shouldReturnBccAsArray() {
    // Given
    MailNotification mailNotification = MailNotification.builder().bcc(List.of("antonio@nelle.vacanze")).build();

    // When
    String[] bccAsArray = mailNotification.bccAsArray();

    // Then
    assertThat(bccAsArray).isNotEmpty().containsExactly("antonio@nelle.vacanze");
  }
}