package ch.sbb.importservice.module.bulkimport.template;

import ch.sbb.atlas.imports.model.terminate.SloidTerminateCsvModel;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StopPointTemplateGenerator {

  public static SloidTerminateCsvModel getSloidTerminateCsvModelExample() {
    return SloidTerminateCsvModel.builder()
        .sloid("ch:1:sloid:1")
        .validTo(LocalDate.of(2022, 12, 31))
        .build();
  }

}
