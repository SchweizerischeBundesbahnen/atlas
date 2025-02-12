package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.lidi.LineVersionModelV2;
import ch.sbb.exportservice.entity.lidi.Line;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class LineJsonProcessor implements ItemProcessor<Line, LineVersionModelV2> {

  @Override
  public LineVersionModelV2 process(Line line) {
    return LineVersionModelV2.builder()
        .build(); // todo
  }

}
