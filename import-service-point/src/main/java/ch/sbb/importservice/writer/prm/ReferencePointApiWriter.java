package ch.sbb.importservice.writer.prm;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointCsvModelContainer;
import ch.sbb.atlas.imports.prm.referencepoint.ReferencePointImportRequestModel;
import ch.sbb.importservice.writer.BaseApiWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@StepScope
public class ReferencePointApiWriter extends BaseApiWriter implements ItemWriter<ReferencePointCsvModelContainer> {

    @Override
    public void write(Chunk<? extends ReferencePointCsvModelContainer> referencePointCsvModelContainers) {
        List<ReferencePointCsvModelContainer> referencePointContainers = new ArrayList<>(referencePointCsvModelContainers.getItems());
        ReferencePointImportRequestModel importRequestModel = new ReferencePointImportRequestModel();
        importRequestModel.setReferencePointCsvModelContainers(referencePointContainers);
        Long stepExecutionId = stepExecution.getId();
        List<ItemImportResult> importResults = prmClient.importReferencePoints(importRequestModel);
        for (ItemImportResult response : importResults) {
            saveItemProcessed(stepExecutionId, response.getItemNumber(), response.getStatus(), response.getMessage());
        }
    }

}
