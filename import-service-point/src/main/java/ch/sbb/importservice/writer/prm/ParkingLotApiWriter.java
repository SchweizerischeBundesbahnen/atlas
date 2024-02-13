package ch.sbb.importservice.writer.prm;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.parkinglot.ParkingLotCsvModelContainer;
import ch.sbb.atlas.imports.prm.parkinglot.ParkingLotImportRequestModel;
import ch.sbb.importservice.writer.BaseApiWriter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@StepScope
public class ParkingLotApiWriter extends BaseApiWriter implements ItemWriter<ParkingLotCsvModelContainer> {

    @Override
    public void write(Chunk<? extends ParkingLotCsvModelContainer> parkingLotCsvModelContainers) {
        List<ParkingLotCsvModelContainer> containers = new ArrayList<>(parkingLotCsvModelContainers.getItems());
        ParkingLotImportRequestModel importRequestModel = new ParkingLotImportRequestModel();
        importRequestModel.setParkingLotCsvModelContainers(containers);
        Long stepExecutionId = stepExecution.getId();
        List<ItemImportResult> importResults = prmClient.importParkingLots(importRequestModel);
        for (ItemImportResult response : importResults) {
            saveItemProcessed(stepExecutionId, response.getItemNumber(), response.getStatus(), response.getMessage());
        }
    }

}
