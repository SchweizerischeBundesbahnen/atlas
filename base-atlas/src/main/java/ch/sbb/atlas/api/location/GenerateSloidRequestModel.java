package ch.sbb.atlas.api.location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record GenerateSloidRequestModel(@NotNull SloidType sloidType,
                                        @NotBlank @Pattern(regexp = "ch:1:sloid:[0-9]+") String sloidPrefix) {

}
