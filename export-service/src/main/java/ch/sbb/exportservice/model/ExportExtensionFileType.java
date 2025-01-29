package ch.sbb.exportservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExportExtensionFileType {

  CSV_EXTENSION(".csv"),
  JSON_EXTENSION(".json");

  private final String extension;
}
