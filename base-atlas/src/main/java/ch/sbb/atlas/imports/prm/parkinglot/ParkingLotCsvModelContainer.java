package ch.sbb.atlas.imports.prm.parkinglot;

import ch.sbb.atlas.api.prm.model.parkinglot.ParkingLotVersionModel;
import ch.sbb.atlas.imports.prm.BasePrmCsvModelContainer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkingLotCsvModelContainer extends BasePrmCsvModelContainer<ParkingLotCsvModel> {

    @JsonIgnore
    public List<ParkingLotVersionModel> getCreateModels() {
        return getCsvModels().stream().map(ParkingLotCsvToModelMapper::toModel).toList();
    }

}
