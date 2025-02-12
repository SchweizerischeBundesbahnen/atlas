package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.lidi.TimetableFieldNumberModel;
import ch.sbb.exportservice.entity.lidi.TimetableFieldNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class TimetableFieldNumberJsonProcessor implements ItemProcessor<TimetableFieldNumber, TimetableFieldNumberModel> {

  @Override
  public TimetableFieldNumberModel process(TimetableFieldNumber timetableFieldNumber) {
    return null; // todo
  }

}
