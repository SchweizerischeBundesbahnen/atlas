package ch.sbb.exportservice.job.prm.wheelchairaccessibility.writer;

import ch.sbb.exportservice.job.prm.wheelchairaccessibility.model.WheelchairAccessibilityCsvModel;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccessibilityFileWriter {

  private final CsvWheelchairAccessibilityWriter csvWheelchairAccessibilityWriter;
  private FlatFileItemWriter<WheelchairAccessibilityCsvModel> itemWriter;

  public void open(ExecutionContext executionContext) {
    this.itemWriter = csvWheelchairAccessibilityWriter.csvWriter(ExportObjectV2.WHEELCHAIR_ACCESSIBILITY, ExportTypeV2.ACTUAL);
    itemWriter.open(executionContext);
  }

  public void write(List<WheelchairAccessibilityCsvModel> accessibilityCsvModels) {
    try {
      itemWriter.write(new Chunk<>(accessibilityCsvModels));
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public void close() {
    itemWriter.close();
  }
}
