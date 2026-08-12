package ch.sbb.timetable.hearing.redact;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.user.administration.security.redact.TthRedactAspect;
import ch.sbb.timetable.hearing.entity.TthDossier;
import ch.sbb.timetable.hearing.entity.TthDossierQuestion;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class TthRedactAspectTest {

  @Test
  void shouldRedactStatementCorrectly() {
    // given
    TthDossier sensitiveDossier = TthDossier.builder()
        .swissCanton(SwissCanton.BERN)
        .topic("Dossier Topic")
        .dossierStatus(DossierStatus.ADDED)
        .internalComment("empty")
        .publicComment("empty")
        .statementIds(List.of(1000L))
        .boContactMail("john.doe@sbb.ch")
        .boDeadlineToAnswer(LocalDate.of(2099, 12, 31))
        .dossierQuestions(List.of(TthDossierQuestion.builder().question("how are you?").answerToCanton("fine").build()))
        .build();

    TthDossier redactedDossier = TthDossier.builder()
        .swissCanton(SwissCanton.BERN)
        .topic("Dossier Topic")
        .dossierStatus(DossierStatus.ADDED)
        .internalComment("*****")
        .publicComment("*****")
        .statementIds(List.of(1000L))
        .boContactMail("john.doe@sbb.ch")
        .boDeadlineToAnswer(LocalDate.of(2099, 12, 31))
        .dossierQuestions(List.of(TthDossierQuestion.builder().question("how are you?").answerToCanton("fine").build()))
        .build();

    // when & then
    Object redactObject = TthRedactAspect.redactObject(sensitiveDossier);
    assertThat(redactObject).usingRecursiveComparison().isEqualTo(redactedDossier);
  }
}
