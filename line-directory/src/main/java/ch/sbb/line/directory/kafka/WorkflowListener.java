package ch.sbb.line.directory.kafka;

import ch.sbb.atlas.base.service.model.exception.NotFoundException;
import ch.sbb.atlas.kafka.model.workflow.WorkflowEvent;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.LineVersionWorkflow;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.LineVersionWorkflowRepository;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = "${kafka.atlas.workflow.topic}", groupId = "${kafka.atlas.workflow.groupId}")
public class WorkflowListener {

  private final LineVersionWorkflowRepository lineVersionWorkflowRepository;
  private final LineVersionRepository lineVersionRepository;

  @KafkaHandler
  public void receiveWorkflowNotification(@Valid WorkflowEvent workflowEvent) {
    LineVersion lineVersion =
        lineVersionRepository.findById(workflowEvent.getBusinessObjectId())
            .orElseThrow(() -> new NotFoundException.IdNotFoundException(
                workflowEvent.getBusinessObjectId()));
    LineVersionWorkflow lineVersionWorkflow = LineVersionWorkflow.builder()
        .workflowId(workflowEvent.getWorkflowId())
        .lineVersionId(lineVersion)
        .build();
    LineVersionWorkflow workflow = lineVersionWorkflowRepository.save(lineVersionWorkflow);

    log.info("Consumed: {}", workflowEvent);
    log.info("Saved: {}", workflow);
  }

}
