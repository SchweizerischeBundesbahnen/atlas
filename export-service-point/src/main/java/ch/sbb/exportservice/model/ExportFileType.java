package ch.sbb.exportservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExportFileType {

  CSV_EXTENSION(".csv"),
  JSON_EXTENSION(".json");

  private String extention;
}
