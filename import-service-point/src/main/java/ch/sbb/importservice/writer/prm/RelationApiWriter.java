package ch.sbb.importservice.writer.prm;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.relation.RelationCsvModelContainer;
import ch.sbb.atlas.imports.prm.relation.RelationImportRequestModel;
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
public class RelationApiWriter extends BaseApiWriter implements ItemWriter<RelationCsvModelContainer> {

    @Override
    public void write(Chunk<? extends RelationCsvModelContainer> relationCsvModelContainers) {
        List<RelationCsvModelContainer> relationContainers = new ArrayList<>(relationCsvModelContainers.getItems());
        RelationImportRequestModel importRequestModel = new RelationImportRequestModel();
        importRequestModel.setRelationCsvModelContainers(relationContainers);
        Long stepExecutionId = stepExecution.getId();
        List<ItemImportResult> importResults = prmClient.importRelations(importRequestModel);
        for (ItemImportResult response : importResults) {
            saveItemProcessed(stepExecutionId, response.getItemNumber(), response.getStatus(), response.getMessage());
        }
    }
}
