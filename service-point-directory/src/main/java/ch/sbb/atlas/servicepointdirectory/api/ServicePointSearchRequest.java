package ch.sbb.atlas.servicepointdirectory.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ServicePointSearchRequest {

    @Schema(description = """
          Search over:
          - ServicePointNumber/DiDok-Number formerly known as UIC-Code
          - Official designation of a location
          - Location abbreviation
          - Long designation of a location""")
    private String value;

}
