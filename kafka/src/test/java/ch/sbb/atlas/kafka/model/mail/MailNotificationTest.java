<<<<<<< HEAD:base-service/src/test/java/ch/sbb/atlas/base/service/model/mail/MailNotificationTest.java
package ch.sbb.atlas.base.service.model.mail;
=======
package ch.sbb.atlas.kafka.model.mail;
>>>>>>> ATLAS-827: Move kafka models to kafka lib:kafka/src/test/java/ch/sbb/atlas/kafka/model/mail/MailNotificationTest.java

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class MailNotificationTest {

  private static final String MAIL = "antonio@nelle.vacanze";

  @Test
  void shouldReturnToAsArray() {
    // Given
    MailNotification mailNotification = MailNotification.builder().to(List.of(MAIL)).build();

    // When
    String[] toAsArray = mailNotification.toAsArray();

    // Then
    assertThat(toAsArray).isNotEmpty().containsExactly(MAIL);
  }

  @Test
  void shouldReturnCcAsArray() {
    // Given
    MailNotification mailNotification = MailNotification.builder().cc(List.of(MAIL)).build();

    // When
    String[] ccAsArray = mailNotification.ccAsArray();

    // Then
    assertThat(ccAsArray).isNotEmpty().containsExactly(MAIL);
  }

  @Test
  void shouldReturnBccAsArray() {
    // Given
    MailNotification mailNotification = MailNotification.builder().bcc(List.of(MAIL)).build();

    // When
    String[] bccAsArray = mailNotification.bccAsArray();

    // Then
    assertThat(bccAsArray).isNotEmpty().containsExactly(MAIL);
  }
}