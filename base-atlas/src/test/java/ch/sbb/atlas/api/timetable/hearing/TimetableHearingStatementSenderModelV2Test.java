package ch.sbb.atlas.api.timetable.hearing;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.BaseValidatorTest;
import jakarta.validation.ConstraintViolation;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TimetableHearingStatementSenderModelV2Test extends BaseValidatorTest {

  TimetableHearingStatementSenderModelV2 timetableHearingStatementSenderModelV2 = new TimetableHearingStatementSenderModelV2();

  @Test
  public void whenValidEmails_thenNoConstraintViolation() {
    timetableHearingStatementSenderModelV2.setEmails(Set.of("maurer@post.ch", "burri@post.ch"));

    Set<ConstraintViolation<TimetableHearingStatementSenderModelV2>> violations = validator.validate(timetableHearingStatementSenderModelV2);

    assertThat(violations.size()).isEqualTo(0);
  }

  @Test
  public void whenMoreThan10EmailsInSet_thenConstraintViolation() {
    timetableHearingStatementSenderModelV2.setEmails(Set.of("maurer@post.ch", "fabienne1.mueller@sbb.ch", "fabienne2.mueller@sbb.ch",
        "fabienne3.mueller@sbb.ch", "fabienne4.mueller@sbb.ch", "fabienne5.mueller@sbb.ch",
        "fabienne6.mueller@sbb.ch", "fabienne7.mueller@sbb.ch", "fabienne8.mueller@sbb.ch",
        "fabienne9.mueller@sbb.ch", "fabienne10.mueller@sbb.ch"));

    Set<ConstraintViolation<TimetableHearingStatementSenderModelV2>> violations = validator.validate(timetableHearingStatementSenderModelV2);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("Minimum 1 email address is required and maximum 10 email addresses are allowed");
  }

  @Test
  public void whenNoEmails_thenConstraintViolation() {
    timetableHearingStatementSenderModelV2.setEmails(new HashSet<>());

    Set<ConstraintViolation<TimetableHearingStatementSenderModelV2>> violations = validator.validate(timetableHearingStatementSenderModelV2);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("Minimum 1 email address is required and maximum 10 email addresses are allowed");
  }

  @Test
  public void whenInvalidEmailForm_thenConstraintViolation() {
    timetableHearingStatementSenderModelV2.setEmails(Set.of("maurer@post.ch", "fabienne1.mueller.sbb.ch"));

    Set<ConstraintViolation<TimetableHearingStatementSenderModelV2>> violations = validator.validate(timetableHearingStatementSenderModelV2);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("must match \"^$|^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$\"");
  }

  @Test
  public void whenEmailLenghtMoreThan100Characters_thenConstraintViolation() {
    timetableHearingStatementSenderModelV2.setEmails(Set.of("maurer@post.ch",
        "maurerfabienne1muellersbbchmaurerfabienne1muellersbbchmaurerfabienne1muellersbbchnne1muellersbbchmaurerfabienne1muellersbbch@post.ch"));

    Set<ConstraintViolation<TimetableHearingStatementSenderModelV2>> violations = validator.validate(timetableHearingStatementSenderModelV2);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations.iterator().next().getMessage()).isEqualTo("size must be between 0 and 100");
  }

}