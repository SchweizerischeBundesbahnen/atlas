package ch.sbb.exportservice.controller;

import ch.sbb.atlas.export.enumeration.ExportFileName;
import ch.sbb.exportservice.model.PrmBatchExportFileName;
import ch.sbb.exportservice.model.SePoDiBatchExportFileName;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Deprecated(forRemoval = true)
public class ExportFileNameConverter implements Converter<String, ExportFileName> {

  @Override
  public ExportFileName convert(String source) { // todo: can source be null?
    try {
      return SePoDiBatchExportFileName.valueOf(source);
    } catch (IllegalArgumentException ignored) {
    }

    return PrmBatchExportFileName.valueOf(source);
  }

}
