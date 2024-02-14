package ch.sbb.atlas.imports.prm.parkinglot;

import ch.sbb.atlas.imports.prm.BasePrmCsvModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkingLotCsvModel extends BasePrmCsvModel {

    @JsonProperty("SLOID")
    private String sloid;

    @JsonProperty("DIDOK_CODE")
    private Integer didokCode;

    @JsonProperty("DESCRIPTION")
    private String description;

    @JsonProperty("INFO")
    private String info;

    @JsonProperty("PLACES_AVAILABLE")
    private Integer placesAvailable;

    @JsonProperty("PRM_PLACES_AVAILABLE")
    private Integer prmPlacesAvailable;

    @JsonProperty("STATUS")
    private Integer status;

    @JsonProperty("DS_SLOID")
    private String dsSloid;

}
