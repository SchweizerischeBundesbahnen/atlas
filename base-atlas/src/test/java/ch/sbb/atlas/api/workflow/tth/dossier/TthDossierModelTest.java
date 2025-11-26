package ch.sbb.atlas.api.workflow.tth.dossier;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.BaseValidatorTest;
import jakarta.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TthDossierModelTest extends BaseValidatorTest {

  @Test
  void shouldNotAllowDossierWithoutStatement() {
    TthDossierModel tthDossierModel = TthDossierModel.builder()
        .topic("Längere Anbindung am Abend")
        .boContactMail("urs@bernmobil.be")
        .boDeadlineToAnswer(LocalDate.now().plusDays(1))
        .build();
    Set<ConstraintViolation<TthDossierModel>> constraintViolations = validator.validate(tthDossierModel);
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  void shouldValidateMail() {
    TthDossierModel tthDossierModel = TthDossierModel.builder()
        .topic("Längere Anbindung am Abend")
        .statementIds(List.of(1L, 2L))
        .dossierStatus(DossierStatus.DOSSIER_BO_CHECK)
        .boContactMail("urs@bernmobil")
        .boDeadlineToAnswer(LocalDate.now().plusDays(1))
        .build();
    Set<ConstraintViolation<TthDossierModel>> constraintViolations = validator.validate(tthDossierModel);
    assertThat(constraintViolations).hasSize(1);
  }
}