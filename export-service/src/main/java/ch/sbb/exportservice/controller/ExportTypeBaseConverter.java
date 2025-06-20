package ch.sbb.exportservice.controller;

import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.exportservice.model.PrmExportType;
import ch.sbb.exportservice.model.SePoDiExportType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Deprecated(forRemoval = true)
public class ExportTypeBaseConverter implements Converter<String, ExportTypeBase> {

  @Override
  public ExportTypeBase convert(String source) {
    String exportType = source.toUpperCase().replace("-", "_");
    try {
      return SePoDiExportType.valueOf(exportType);
    } catch (IllegalArgumentException ignored) {
    }

    return PrmExportType.valueOf(exportType);
  }

}
