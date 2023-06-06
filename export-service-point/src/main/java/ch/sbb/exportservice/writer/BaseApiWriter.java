package ch.sbb.exportservice.writer;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;

public abstract class BaseApiWriter {

  protected StepExecution stepExecution;

  @BeforeStep
  public void getStepExecutionData(StepExecution stepExecution) {
    this.stepExecution = stepExecution;
  }

  protected void saveFileExported(Long stepExecutionId, Integer number, String exportStatus, String message) {

  }

}
