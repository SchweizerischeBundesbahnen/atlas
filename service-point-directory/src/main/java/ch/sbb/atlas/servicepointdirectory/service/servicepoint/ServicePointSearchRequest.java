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
public class ServicePointSearchRequest {

    @Schema(description = """
          Search over:
          - ServicePointNumber/DiDok-Number formerly known as UIC-Code
          - Official designation of a location
          - Location abbreviation
          - Long designation of a location""")
    private String value;

}
