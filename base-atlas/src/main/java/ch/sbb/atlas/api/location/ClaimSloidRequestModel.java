package ch.sbb.atlas.api.location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ClaimSloidRequestModel(
    @NotBlank @Pattern(regexp = "(ch:1:sloid:[0-9]+$)|(ch:1:sloid:[0-9]+:[0-9]+$)|(ch:1:sloid:[0-9]+:[0-9]+:[0-9]+$)") String sloid) {

}
