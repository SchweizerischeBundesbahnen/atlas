package ch.sbb.importservice.writer.prm;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModelContainer;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointImportRequestModel;
import ch.sbb.importservice.writer.BaseApiWriter;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.ArrayList;
import java.util.List;

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
