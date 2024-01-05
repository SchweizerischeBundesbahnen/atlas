package ch.sbb.atlas.api.location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GenerateSloidRequestModel(@NotNull SloidType sloidType, @NotBlank String sloidPrefix) {

}
