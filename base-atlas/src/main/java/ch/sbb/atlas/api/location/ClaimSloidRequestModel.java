package ch.sbb.atlas.api.location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClaimSloidRequestModel(@NotNull(message = "SloidType must not be null") SloidType sloidType,
                                     @NotBlank(message = "sloid must not be blank") String sloid) {

}
