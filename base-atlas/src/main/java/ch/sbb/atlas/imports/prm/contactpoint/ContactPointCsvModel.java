package ch.sbb.atlas.imports.prm.contactpoint;

import ch.sbb.atlas.imports.prm.BasePrmCsvModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactPointCsvModel extends BasePrmCsvModel {

    @JsonProperty("SLOID")
    private String sloid;

    @JsonProperty("DIDOK_CODE")
    private Integer didokCode;

    @JsonProperty("DESCRIPTION")
    private String description;

    @JsonProperty("COMPL_INFOS")
    private String infos;

    @JsonProperty("INDUCTION_LOOP")
    private Integer inductionLoop;

    @JsonProperty("OPEN_HOURS")
    private String openHours;

    @JsonProperty("STATUS")
    private String status;

    @JsonProperty("WHEELCHAIR_ACCESS")
    private Integer wheelChairAccess;

    @JsonProperty("DS_SLOID")
    private String dsSloid;
}
