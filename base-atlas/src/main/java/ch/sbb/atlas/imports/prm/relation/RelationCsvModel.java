package ch.sbb.atlas.imports.prm.relation;

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
public class RelationCsvModel extends BasePrmCsvModel {

    @JsonProperty("EL_SLOID")
    private String sloid;

    @JsonProperty("RP_SLOID")
    private String rpSloid;

    @JsonProperty("DIDOK_CODE")
    private Integer didokCode;

    @JsonProperty("TACT_VISUAL_MARKS")
    private Integer tactVisualMarks;

    @JsonProperty("CONTRASTING_AREAS")
    private Integer contrastingAreas;

    @JsonProperty("STEP_FREE_ACCESS")
    private Integer stepFreeAccess;

    @JsonProperty("EL_STATUS")
    private Integer status;

    @JsonProperty("EL_TYPE")
    private String elType;

    @JsonProperty("DS_SLOID")
    private String dsSloid;
}
