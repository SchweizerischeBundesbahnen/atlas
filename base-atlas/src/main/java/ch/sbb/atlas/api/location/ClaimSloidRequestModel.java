package ch.sbb.atlas.api.location;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ClaimSloidRequestModel(
    @NotBlank @Pattern(regexp = "ch:1:sloid:[0-9]+" + AtlasCharacterSetsRegex.SID4PT) String sloid) {

}
