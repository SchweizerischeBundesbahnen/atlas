package ch.sbb.importservice.service.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.client.user.administration.UserAdministrationClient;
import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.importservice.exception.ImporterMailNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BulkImporterMailServiceTest {

  @Mock
  private UserAdministrationClient userAdministrationClient;

  private BulkImporterMailService bulkImporterMailService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    bulkImporterMailService = new BulkImporterMailService(userAdministrationClient);
  }

  @Test
  void shouldReturnMailOfCurrentUser() {
    when(userAdministrationClient.getCurrentUser()).thenReturn(UserModel.builder().mail("mail@here.ch").build());

    String mailOfCurrentUser = bulkImporterMailService.getMailOfCurrentUser();
    assertThat(mailOfCurrentUser).isEqualTo("mail@here.ch");
  }

  @Test
  void shouldCheckImporterMail() {
    when(userAdministrationClient.getCurrentUser()).thenReturn(UserModel.builder().mail("mail@here.ch").build());
    assertThatNoException().isThrownBy(() -> bulkImporterMailService.checkImporterHasMail());
  }

  @Test
  void shouldThrowImporterMailException() {
    when(userAdministrationClient.getCurrentUser()).thenReturn(UserModel.builder().build());
    assertThatExceptionOfType(ImporterMailNotFoundException.class).isThrownBy(
        () -> bulkImporterMailService.checkImporterHasMail());
  }
}