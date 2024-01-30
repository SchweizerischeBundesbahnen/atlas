package ch.sbb.atlas.imports.prm.referencepoint;

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
public class ReferencePointCsvModel extends BasePrmCsvModel {

    @JsonProperty("SLOID")
    private String sloid;

    @JsonProperty("DIDOK_CODE")
    private Integer didokCode;

    @JsonProperty("DESCRIPTION")
    private String description;

    @JsonProperty("EXPORT")
    private Integer export;

    @JsonProperty("INFOS")
    private String infos;

    @JsonProperty("RP_TYPE")
    private Integer rpType;

    @JsonProperty("STATUS")
    private Integer status;

    @JsonProperty("DS_SLOID")
    private String dsSloid;

}
