package ch.sbb.importservice.writer.prm;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModelContainer;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointImportRequestModel;
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
public class ContactPointApiWriter extends BaseApiWriter implements ItemWriter<ContactPointCsvModelContainer> {
    @Override
    public void write(Chunk<? extends ContactPointCsvModelContainer> contactPointCsvModelContainers) {
        List<ContactPointCsvModelContainer> contactPointContainers = new ArrayList<>(contactPointCsvModelContainers.getItems());
        ContactPointImportRequestModel importRequestModel = new ContactPointImportRequestModel();
        importRequestModel.setContactPointCsvModelContainers(contactPointContainers);
        Long stepExecutionId = stepExecution.getId();
        List<ItemImportResult> importResults = prmClient.importContactPoints(importRequestModel);
        for (ItemImportResult response : importResults) {
            saveItemProcessed(stepExecutionId, response.getItemNumber(), response.getStatus(), response.getMessage());
        }
    }
}
