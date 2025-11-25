package ch.sbb.atlas.api.timetable.hearing;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.BaseValidatorTest;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TimetableHearingStatementModelV2Test extends BaseValidatorTest {

  @Test
  void shouldBeValidStatement() {
    TimetableHearingStatementModelV2 model = TimetableHearingStatementModelV2.builder()
        .statement("Ich möchte gerne mehr Sitzplätze im Bus.")
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .emails(Set.of("urs@home.ch"))
            .build())
        .swissCanton(SwissCanton.BERN)
        .build();

    Set<ConstraintViolation<TimetableHearingStatementModelV2>> constraintViolations = validator.validate(model);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldRequireSender() {
    TimetableHearingStatementModelV2 model = TimetableHearingStatementModelV2.builder()
        .statement("Ich möchte gerne mehr Sitzplätze im Bus.")
        .swissCanton(SwissCanton.BERN)
        .build();

    Set<ConstraintViolation<TimetableHearingStatementModelV2>> constraintViolations = validator.validate(model);
    assertThat(constraintViolations).hasSize(1);
  }

  @Test
  void shouldBeValidStatementWithAnonymousCheckbox() {
    TimetableHearingStatementModelV2 model = TimetableHearingStatementModelV2.builder()
        .statement("Ich möchte gerne mehr Sitzplätze im Bus.")
        .statementAnonymous(true)
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .emails(Set.of("urs@home.ch"))
            .build())
        .swissCanton(SwissCanton.BERN)
        .build();

    Set<ConstraintViolation<TimetableHearingStatementModelV2>> constraintViolations = validator.validate(model);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldBeValidStatementWithAnonymousStatement() {
    TimetableHearingStatementModelV2 model = TimetableHearingStatementModelV2.builder()
        .statement("Ich möchte gerne mehr Sitzplätze im Bus.")
        .anonymousStatement("Jemand möchte mehr Plätze")
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .emails(Set.of("urs@home.ch"))
            .build())
        .swissCanton(SwissCanton.BERN)
        .build();

    Set<ConstraintViolation<TimetableHearingStatementModelV2>> constraintViolations = validator.validate(model);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldBeInvalidStatementWithAnonymousStatementAndCheckbox() {
    TimetableHearingStatementModelV2 model = TimetableHearingStatementModelV2.builder()
        .statement("Ich möchte gerne mehr Sitzplätze im Bus.")
        .statementAnonymous(true)
        .anonymousStatement("Jemand möchte mehr Plätze")
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .emails(Set.of("urs@home.ch"))
            .build())
        .swissCanton(SwissCanton.BERN)
        .build();

    Set<ConstraintViolation<TimetableHearingStatementModelV2>> constraintViolations = validator.validate(model);
    assertThat(constraintViolations).isNotEmpty();
  }
}