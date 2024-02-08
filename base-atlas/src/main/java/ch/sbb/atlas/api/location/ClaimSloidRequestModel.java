package ch.sbb.atlas.api.location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClaimSloidRequestModel(@NotNull SloidType sloidType, @NotBlank String sloid) {

}
