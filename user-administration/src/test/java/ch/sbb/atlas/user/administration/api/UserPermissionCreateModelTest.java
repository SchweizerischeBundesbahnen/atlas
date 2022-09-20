package ch.sbb.atlas.user.administration.api;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;

public class UserPermissionCreateModelTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldValidateUniqueApplicationType() {
        // Given
        UserPermissionCreateModel createModel = UserPermissionCreateModel.builder()
                .sbbUserId("u123456")
                .permissions(List.of(
                        UserPermissionModel.builder()
                                .sboids(List.of())
                                .role(ApplicationRole.WRITER)
                                .application(ApplicationType.TTFN)
                                .build(),
                        UserPermissionModel.builder()
                                .sboids(List.of())
                                .role(ApplicationRole.WRITER)
                                .application(ApplicationType.TTFN)
                                .build()
                )).
                build();
        // When
        Set<ConstraintViolation<UserPermissionCreateModel>> constraintViolations = validator.validate(
                createModel);

        // Then
        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString("applicationTypeUniqueInPermissions");
    }

    @Test
    void shouldValidateSboidsEmptyWhenNotWriterRole(){
        // Given
        UserPermissionCreateModel createModel = UserPermissionCreateModel.builder()
                .sbbUserId("u123456")
                .permissions(List.of(
                        UserPermissionModel.builder()
                                .sboids(List.of("ch:1:sboid:test"))
                                .role(ApplicationRole.SUPERVISOR)
                                .application(ApplicationType.TTFN)
                                .build()
                )).
                build();
        // When
        Set<ConstraintViolation<UserPermissionCreateModel>> constraintViolations = validator.validate(
                createModel);

        // Then
        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString("sboidsEmptyWhenNotWriterOrBodi");
    }

    @Test
    void shouldValidateSboidsEmptyWhenApplicationTypeBodi(){
        // Given
        UserPermissionCreateModel createModel = UserPermissionCreateModel.builder()
                .sbbUserId("u123456")
                .permissions(List.of(
                        UserPermissionModel.builder()
                                .sboids(List.of("ch:1:sboid:test"))
                                .role(ApplicationRole.WRITER)
                                .application(ApplicationType.BODI)
                                .build()
                )).
                build();
        // When
        Set<ConstraintViolation<UserPermissionCreateModel>> constraintViolations = validator.validate(
                createModel);

        // Then
        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.iterator().next().getPropertyPath()).hasToString("sboidsEmptyWhenNotWriterOrBodi");
    }

}
