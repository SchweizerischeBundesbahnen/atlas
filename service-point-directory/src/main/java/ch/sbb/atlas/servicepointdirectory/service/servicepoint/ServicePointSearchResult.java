package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ServicePointSearchResult {

    @Schema(description = "DiDok-Number formerly known as UIC-Code, combination of uicCountryCode and numberShort.")
    private Integer number;

    @Schema(description = "Official designation of a location that must be used by all recipients"
            , example = "Biel/Bienne Bözingenfeld/Champ")
    private String designationOfficial;

    @Schema(description = "Unique key for service points which is used in the customer information."
            , example = "ch:1:sloid:7000")
    private String sloid;

}
