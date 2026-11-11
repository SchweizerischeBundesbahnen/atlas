package ch.sbb.atlas.api.workflow;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.BaseValidatorTest;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PersonModelTest extends BaseValidatorTest {

  @Test
  void shouldValidateWithNullFirstNameAndLastName() {
    PersonModel person = getPersonBuilder().build();
    Set<ConstraintViolation<PersonModel>> constraintViolations = validator.validate(person);
    assertThat(constraintViolations).isEmpty();
  }

  @Test
  void shouldFailToValidateWithBlankFirstName() {
    PersonModel person = getPersonBuilder().firstName("   ").build();
    Set<ConstraintViolation<PersonModel>> constraintViolations = validator.validate(person);
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString("firstName");
  }

  @Test
  void shouldFailToValidateWithBlankLastName() {
    PersonModel person = getPersonBuilder().lastName("   ").build();
    Set<ConstraintViolation<PersonModel>> constraintViolations = validator.validate(person);
    assertThat(constraintViolations).hasSize(1);
    assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString("lastName");
  }

  private static PersonModel.PersonModelBuilder<?, ?> getPersonBuilder() {
    return PersonModel.builder().personFunction("Officer");
  }

}
