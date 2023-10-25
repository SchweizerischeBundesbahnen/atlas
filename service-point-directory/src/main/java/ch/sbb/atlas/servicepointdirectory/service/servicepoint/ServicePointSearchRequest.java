package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull(message = "You must enter at least 2 digits to start a search!")
    @Size(min = 2, message = "You must enter at least 2 digits to start a search!")
    private String value;

}
