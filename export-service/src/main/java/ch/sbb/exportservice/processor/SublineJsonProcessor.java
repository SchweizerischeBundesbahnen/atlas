package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.lidi.ReadSublineVersionModelV2;
import ch.sbb.exportservice.entity.lidi.Subline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class SublineJsonProcessor implements ItemProcessor<Subline, ReadSublineVersionModelV2> {

  @Override
  public ReadSublineVersionModelV2 process(Subline subline) {
    return null; // todo
  }

}
