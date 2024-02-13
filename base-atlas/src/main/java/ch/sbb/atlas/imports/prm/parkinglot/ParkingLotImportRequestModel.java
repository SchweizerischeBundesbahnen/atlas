package ch.sbb.atlas.imports.prm.parkinglot;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "ParkingLotImportRequest")
public class ParkingLotImportRequestModel {

    @Schema(name = "List of parkingLotCsvModelContainers to import")
    @NotNull
    @NotEmpty
    private List<ParkingLotCsvModelContainer> parkingLotCsvModelContainers;

}
